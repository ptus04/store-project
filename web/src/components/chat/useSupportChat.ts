import type { IMessage } from "@stomp/stompjs";
import { Client } from "@stomp/stompjs";
import type { FormEvent } from "react";
import { useEffect, useRef, useState } from "react";

export interface ChatMessage {
  sessionId: string;
  sender: "USER" | "STAFF" | "AI";
  content: string;
  timestamp: number;
}

export interface SupportSession {
  sessionId: string;
  active: boolean;
  lastMessage: string;
  customerName?: string;
  staffName?: string | null;
}

export function getSenderName(
  msg: ChatMessage,
  activeSession: SupportSession | undefined,
  staffLabel: string,
): string {
  if (msg.sender === "STAFF") return staffLabel;
  if (msg.sender === "AI") return "Chatbot (AI)";
  if (msg.sender === "USER") {
    return activeSession?.customerName &&
      activeSession.customerName !== "Khách vãng lai"
      ? activeSession.customerName
      : "Khách hàng";
  }
  return "Khách hàng";
}

function getCurrentStaffName() {
  try {
    const userStr = localStorage.getItem("user");
    if (!userStr) return "Nhân viên";

    const currentUser = JSON.parse(userStr);
    return currentUser?.name || "Nhân viên";
  } catch {
    return "Nhân viên";
  }
}

function playNotificationSound() {
  try {
    const audioCtx = new (
      globalThis.AudioContext ||
      (globalThis as unknown as { webkitAudioContext: typeof AudioContext })
        .webkitAudioContext
    )();
    const oscillator = audioCtx.createOscillator();
    const gainNode = audioCtx.createGain();

    oscillator.connect(gainNode);
    gainNode.connect(audioCtx.destination);

    oscillator.type = "sine";
    oscillator.frequency.setValueAtTime(587.33, audioCtx.currentTime);
    gainNode.gain.setValueAtTime(0.08, audioCtx.currentTime);

    oscillator.start();
    oscillator.stop(audioCtx.currentTime + 0.15);
  } catch (error) {
    console.warn("Could not play audio notification:", error);
  }
}

function processSessionUpdate(prev: SupportSession[], session: SupportSession) {
  const sessionList = [...prev];
  if (!session.active) {
    return sessionList.filter((item) => item.sessionId !== session.sessionId);
  }
  const index = sessionList.findIndex(
    (item) => item.sessionId === session.sessionId,
  );
  if (index !== -1) {
    sessionList[index] = session;
    return sessionList;
  }
  playNotificationSound();
  return [session, ...sessionList];
}

function processChatMessageUpdate(
  prev: Record<string, ChatMessage[]>,
  selectedSession: string,
  chatMsg: ChatMessage,
) {
  const sessionMessages = prev[selectedSession] || [];
  const isDuplicate = sessionMessages.some(
    (msg) =>
      msg.timestamp === chatMsg.timestamp && msg.content === chatMsg.content,
  );
  if (isDuplicate) return prev;
  if (chatMsg.sender === "USER") playNotificationSound();
  return { ...prev, [selectedSession]: [...sessionMessages, chatMsg] };
}

export function useSupportChat() {
  const [sessions, setSessions] = useState<SupportSession[]>([]);
  const [selectedSession, setSelectedSession] = useState<string | null>(null);
  const [messages, setMessages] = useState<Record<string, ChatMessage[]>>({});
  const [inputText, setInputText] = useState("");
  const [connected, setConnected] = useState(false);

  const stompClientRef = useRef<Client | null>(null);

  const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8080";
  const token = localStorage.getItem("token");
  const currentStaffName = getCurrentStaffName();

  useEffect(() => {
    if (!token) return;

    async function fetchSessions() {
      try {
        const response = await fetch(`${apiUrl}/api/support/sessions`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
          const data: SupportSession[] = await response.json();
          setSessions(data.filter((session) => session.active));
        }
      } catch (error) {
        console.error("Failed to fetch support sessions:", error);
      }
    }

    fetchSessions();
  }, [apiUrl, token]);

  useEffect(() => {
    if (!token) return;

    const brokerUrl = apiUrl.replace(/^http/, "ws") + "/ws";

    const client = new Client({
      brokerURL: brokerUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    const handleSupportRequest = (message: IMessage) => {
      const session: SupportSession = JSON.parse(message.body);
      setSessions((prev) => processSessionUpdate(prev, session));
    };

    client.onConnect = () => {
      setConnected(true);
      client.subscribe("/topic/support/requests", handleSupportRequest);
    };

    client.onDisconnect = () => {
      setConnected(false);
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [apiUrl, token]);

  useEffect(() => {
    if (!selectedSession || !stompClientRef.current || !connected) return;

    const handleIncomingMessage = (message: IMessage) => {
      const chatMsg: ChatMessage = JSON.parse(message.body);
      setMessages((prev) =>
        processChatMessageUpdate(prev, selectedSession, chatMsg),
      );
    };

    const subscription = stompClientRef.current.subscribe(
      `/topic/chat/${selectedSession}`,
      handleIncomingMessage,
    );

    return () => {
      subscription.unsubscribe();
    };
  }, [selectedSession, connected]);

  useEffect(() => {
    if (!selectedSession || !token) return;

    const currentSession = selectedSession;

    async function loadHistory() {
      try {
        const response = await fetch(
          `${apiUrl}/api/chat/history/${currentSession}`,
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
      } catch (error) {
        console.error(
          "Failed to load chat history for session:",
          currentSession,
          error,
        );
      }
    }

    loadHistory();
  }, [selectedSession, apiUrl, token]);

  const handleSendMessage = (event: FormEvent) => {
    event.preventDefault();

    if (
      !inputText.trim() ||
      !selectedSession ||
      !stompClientRef.current ||
      !connected
    ) {
      return;
    }

    const chatMsg = {
      sessionId: selectedSession,
      sender: "STAFF",
      content: inputText.trim(),
      senderName: currentStaffName,
    };

    stompClientRef.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(chatMsg),
    });

    setInputText("");
  };

  const handleResolveSession = (sessionId: string) => {
    if (stompClientRef.current && connected) {
      stompClientRef.current.publish({
        destination: "/app/chat.send",
        body: JSON.stringify({
          sessionId,
          sender: "STAFF",
          content:
            "Cuộc hội thoại đã được nhân viên hỗ trợ hoàn thành. Cảm ơn quý khách!",
          senderName: currentStaffName,
        }),
      });
    }

    fetch(`${apiUrl}/api/support/request`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ sessionId, active: "false" }),
    });

    setSessions((prev) =>
      prev.filter((session) => session.sessionId !== sessionId),
    );
    if (selectedSession === sessionId) {
      setSelectedSession(null);
    }
  };

  const handleSelectSession = async (session: SupportSession) => {
    if (session.staffName && session.staffName !== currentStaffName) {
      setSelectedSession(session.sessionId);
      return;
    }

    if (!session.staffName) {
      try {
        const response = await fetch(`${apiUrl}/api/support/assign`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            sessionId: session.sessionId,
            staffName: currentStaffName,
          }),
        });

        if (!response.ok) {
          if (response.status === 409) {
            const data = await response.json();
            alert(
              data.message ||
                "Cuộc hội thoại đã được nhân viên khác nhận hỗ trợ.",
            );
            return;
          }

          console.error("Failed to assign session");
          return;
        }
      } catch (error) {
        console.error("Failed to claim session:", error);
      }
    }

    setSelectedSession(session.sessionId);
  };

  const handleReleaseSession = async (sessionId: string) => {
    try {
      const response = await fetch(`${apiUrl}/api/support/assign`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          sessionId,
          staffName: "",
        }),
      });

      if (response.ok) {
        setSelectedSession(null);
      }
    } catch (error) {
      console.error("Failed to release session:", error);
    }
  };

  const activeSession = sessions.find(
    (session) => session.sessionId === selectedSession,
  );
  const isReadOnly = activeSession?.staffName
    ? activeSession.staffName !== currentStaffName
    : false;

  return {
    sessions,
    selectedSession,
    setSelectedSession,
    messages,
    inputText,
    setInputText,
    connected,
    currentStaffName,
    activeSession,
    isReadOnly,
    handleSendMessage,
    handleResolveSession,
    handleReleaseSession,
    handleSelectSession,
  };
}
