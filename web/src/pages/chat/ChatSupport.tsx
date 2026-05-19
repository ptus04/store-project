import { useEffect, useState, useRef } from "react";
import { Client } from "@stomp/stompjs";

interface ChatMessage {
  sessionId: string;
  sender: "USER" | "STAFF" | "AI";
  content: string;
  timestamp: number;
}

interface SupportSession {
  sessionId: string;
  active: boolean;
  lastMessage: string;
}

export default function ChatSupport() {
  const [sessions, setSessions] = useState<SupportSession[]>([]);
  const [selectedSession, setSelectedSession] = useState<string | null>(null);
  const [messages, setMessages] = useState<Record<string, ChatMessage[]>>({});
  const [inputText, setInputText] = useState("");
  const [connected, setConnected] = useState(false);

  const stompClientRef = useRef<Client | null>(null);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
  const token = localStorage.getItem("token");

  // Play a soft notification sound
  const playNotificationSound = () => {
    try {
      const audioCtx = new (
        window.AudioContext ||
        (window as unknown as { webkitAudioContext: typeof AudioContext })
          .webkitAudioContext
      )();
      const oscillator = audioCtx.createOscillator();
      const gainNode = audioCtx.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(audioCtx.destination);

      oscillator.type = "sine";
      oscillator.frequency.setValueAtTime(587.33, audioCtx.currentTime); // D5 note
      gainNode.gain.setValueAtTime(0.1, audioCtx.currentTime);

      oscillator.start();
      oscillator.stop(audioCtx.currentTime + 0.15);
    } catch (e) {
      console.warn("Could not play audio notification:", e);
    }
  };

  // Scroll to bottom of chat
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, selectedSession]);

  // Load existing active sessions from backend
  useEffect(() => {
    async function fetchSessions() {
      try {
        const response = await fetch(`${API_URL}/api/support/sessions`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.ok) {
          const data: SupportSession[] = await response.json();
          setSessions(data.filter((s) => s.active));
        }
      } catch (err) {
        console.error("Failed to fetch support sessions:", err);
      }
    }
    fetchSessions();
  }, [API_URL, token]);

  // Connect to STOMP WebSocket Server
  useEffect(() => {
    const brokerUrl = API_URL.replace(/^http/, "ws") + "/ws";

    const client = new Client({
      brokerURL: brokerUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      setConnected(true);
      console.log("Connected to STOMP WebSocket server");

      // Subscribe to support requests notifications
      client.subscribe("/topic/support/requests", (message) => {
        const session: SupportSession = JSON.parse(message.body);

        setSessions((prev) => {
          // If session is marked inactive, remove it
          if (!session.active) {
            return prev.filter((s) => s.sessionId !== session.sessionId);
          }
          // Otherwise, add or update it
          const exists = prev.some((s) => s.sessionId === session.sessionId);
          if (exists) {
            return prev.map((s) =>
              s.sessionId === session.sessionId ? session : s,
            );
          } else {
            playNotificationSound();
            return [session, ...prev];
          }
        });
      });
    };

    client.onDisconnect = () => {
      setConnected(false);
      console.log("Disconnected from STOMP WebSocket server");
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [API_URL]);

  // Subscribe to selected session's chat topic
  useEffect(() => {
    if (!selectedSession || !stompClientRef.current || !connected) return;

    const client = stompClientRef.current;

    const subscription = client.subscribe(
      `/topic/chat/${selectedSession}`,
      (message) => {
        const chatMsg: ChatMessage = JSON.parse(message.body);

        setMessages((prev) => {
          const sessionMsgs = prev[selectedSession] || [];
          // Avoid duplicate messages
          const exists = sessionMsgs.some(
            (m) =>
              m.timestamp === chatMsg.timestamp &&
              m.content === chatMsg.content,
          );
          if (exists) return prev;

          if (chatMsg.sender === "USER") {
            playNotificationSound();
          }

          return {
            ...prev,
            [selectedSession]: [...sessionMsgs, chatMsg],
          };
        });
      },
    );

    return () => {
      subscription.unsubscribe();
    };
  }, [selectedSession, connected]);

  // Load chat history when selected session changes
  useEffect(() => {
    if (!selectedSession) return;
    const currentSession: string = selectedSession;

    async function loadHistory() {
      try {
        const response = await fetch(
          `${API_URL}/api/chat/history/${currentSession}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        );
        if (response.ok) {
          const data: ChatMessage[] = await response.json();
          setMessages((prev) => ({
            ...prev,
            [currentSession]: data,
          }));
        }
      } catch (err) {
        console.error(
          "Failed to load chat history for session:",
          currentSession,
          err,
        );
      }
    }

    loadHistory();
  }, [selectedSession, API_URL, token]);

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (
      !inputText.trim() ||
      !selectedSession ||
      !stompClientRef.current ||
      !connected
    )
      return;

    const chatMsg = {
      sessionId: selectedSession,
      sender: "STAFF",
      content: inputText.trim(),
    };

    stompClientRef.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(chatMsg),
    });

    setInputText("");
  };

  const handleResolveSession = (sessionId: string) => {
    // Notify user chat that agent resolved session
    if (stompClientRef.current && connected) {
      stompClientRef.current.publish({
        destination: "/app/chat.send",
        body: JSON.stringify({
          sessionId: sessionId,
          sender: "STAFF",
          content:
            "Cuộc hội thoại đã được nhân viên hỗ trợ hoàn thành. Cảm ơn quý khách!",
        }),
      });
    }

    // Set active = false
    fetch(`${API_URL}/api/support/request`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ sessionId, active: "false" }),
    });

    setSessions((prev) => prev.filter((s) => s.sessionId !== sessionId));
    if (selectedSession === sessionId) {
      setSelectedSession(null);
    }
  };

  return (
    <main className="p-gutter bg-background flex h-[calc(100vh-80px)] flex-col">
      <div className="max-w-container-max mx-auto flex w-full flex-1 gap-6 overflow-hidden pb-6">
        {/* Left Side: Session List */}
        <div className="bg-surface-container-lowest border-outline-variant flex w-80 flex-col border">
          <div className="border-outline-variant border-b p-4">
            <div className="flex items-center justify-between">
              <h2 className="text-body-lg text-primary font-bold">
                Khách hàng đang chờ
              </h2>
              <span
                className={`inline-block h-2.5 w-2.5 rounded-full ${connected ? "bg-green-500" : "bg-red-500"}`}
                title={connected ? "Đã kết nối Realtime" : "Mất kết nối"}
              />
            </div>
            <p className="text-secondary mt-1 text-xs">
              Danh sách các phiên yêu cầu hỗ trợ trực tiếp.
            </p>
          </div>

          <div className="flex-1 space-y-1 overflow-y-auto p-2">
            {sessions.length === 0 ? (
              <div className="text-secondary p-8 text-center text-xs">
                Hiện tại không có yêu cầu hỗ trợ nào.
              </div>
            ) : (
              sessions.map((s) => (
                <button
                  key={s.sessionId}
                  onClick={() => setSelectedSession(s.sessionId)}
                  className={`flex w-full flex-col gap-1 rounded p-3 text-left transition-colors ${
                    selectedSession === s.sessionId
                      ? "bg-surface-container text-primary border-l-4 border-black font-medium"
                      : "hover:bg-surface-container-low text-secondary"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="text-label-sm truncate font-semibold">
                      Session ID: {s.sessionId.substring(0, 12)}...
                    </span>
                    <span className="rounded-full bg-emerald-100 px-1.5 py-0.5 text-[10px] font-medium text-emerald-800">
                      Đang chờ
                    </span>
                  </div>
                  <p className="mt-1 truncate text-xs text-neutral-500">
                    {s.lastMessage}
                  </p>
                </button>
              ))
            )}
          </div>
        </div>

        {/* Right Side: Chat Window */}
        <div className="bg-surface-container-lowest border-outline-variant flex flex-1 flex-col overflow-hidden border">
          {selectedSession ? (
            <>
              {/* Chat Header */}
              <div className="border-outline-variant bg-surface-container-low flex items-center justify-between border-b p-4">
                <div>
                  <h3 className="text-body-md text-primary font-bold">
                    Đang chat với Session: {selectedSession}
                  </h3>
                  <span className="mt-0.5 flex items-center gap-1 text-xs font-medium text-green-600">
                    <span className="h-1.5 w-1.5 animate-ping rounded-full bg-green-500" />{" "}
                    Trực tuyến
                  </span>
                </div>
                <button
                  onClick={() => handleResolveSession(selectedSession)}
                  className="rounded bg-black px-4 py-2 text-xs font-semibold text-white transition-colors hover:bg-neutral-800"
                >
                  Đã giải quyết / Đóng Chat
                </button>
              </div>

              {/* Message History */}
              <div className="flex-1 space-y-4 overflow-y-auto bg-neutral-50 p-4">
                {(messages[selectedSession] || []).length === 0 ? (
                  <div className="text-secondary p-8 text-center text-xs">
                    Bắt đầu cuộc trò chuyện. Hãy gửi lời chào đến khách hàng!
                  </div>
                ) : (
                  (messages[selectedSession] || []).map((msg, index) => {
                    const isStaff = msg.sender === "STAFF";
                    return (
                      <div
                        key={index}
                        className={`flex max-w-[70%] flex-col ${isStaff ? "ml-auto items-end" : "mr-auto items-start"}`}
                      >
                        <span className="mr-1 mb-1 ml-1 text-[10px] font-medium text-neutral-400">
                          {isStaff ? "Bạn (Nhân viên)" : "Khách hàng"}
                        </span>
                        <div
                          className={`rounded-2xl px-4 py-2.5 text-sm leading-relaxed shadow-sm ${
                            isStaff
                              ? "rounded-tr-sm bg-black text-white"
                              : "rounded-tl-sm border border-neutral-200 bg-white text-neutral-800"
                          }`}
                        >
                          {msg.content}
                        </div>
                      </div>
                    );
                  })
                )}
                <div ref={messagesEndRef} />
              </div>

              {/* Chat Input */}
              <form
                onSubmit={handleSendMessage}
                className="border-outline-variant flex gap-2 border-t p-4"
              >
                <input
                  type="text"
                  value={inputText}
                  onChange={(e) => setInputText(e.target.value)}
                  placeholder="Nhập tin nhắn trả lời khách hàng..."
                  className="border-outline-variant flex-1 rounded border px-4 py-2.5 text-sm focus:border-black focus:outline-none"
                />
                <button
                  type="submit"
                  disabled={!inputText.trim()}
                  className="rounded bg-black px-6 py-2.5 text-sm font-semibold text-white transition-all hover:bg-neutral-800 disabled:opacity-50"
                >
                  Gửi
                </button>
              </form>
            </>
          ) : (
            <div className="text-secondary flex flex-1 flex-col items-center justify-center p-8">
              <span className="material-symbols-outlined text-[64px] text-neutral-300">
                chat
              </span>
              <h3 className="text-body-lg mt-4 font-bold text-neutral-800">
                Cổng hỗ trợ khách hàng
              </h3>
              <p className="mt-1 max-w-sm text-center text-xs text-neutral-500">
                Chọn một khách hàng ở danh sách bên trái để bắt đầu trò chuyện
                hỗ trợ trực tiếp.
              </p>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
