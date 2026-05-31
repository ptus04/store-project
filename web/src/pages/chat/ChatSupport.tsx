import { useEffect, useRef } from "react";
import {
  useSupportChat,
  getSenderName,
} from "@components/chat/useSupportChat.ts";

export default function ChatSupport() {
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  const {
    sessions,
    selectedSession,
    messages,
    inputText,
    setInputText,
    connected,
    currentStaffName,
    activeSession,
    isReadOnly,
    handleSendMessage,
    handleResolveSession,

    handleSelectSession,
  } = useSupportChat();

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, selectedSession]);

  return (
    <main className="p-gutter bg-background flex h-[calc(100vh-80px)] flex-col">
      <div className="max-w-container-max mx-auto flex w-full flex-1 gap-6 overflow-hidden pb-6">
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
              sessions.map((session) => {
                const isClaimedByMe = session.staffName === currentStaffName;
                const isClaimedByOther = session.staffName && !isClaimedByMe;

                let badgeColor =
                  "bg-emerald-100 text-emerald-800 animate-pulse";
                let badgeText = "Chưa nhận";

                if (isClaimedByMe) {
                  badgeColor = "bg-blue-100 text-blue-800 font-semibold";
                  badgeText = "Bạn hỗ trợ";
                } else if (isClaimedByOther) {
                  badgeColor = "bg-purple-100 text-purple-800";
                  badgeText = `Hỗ trợ: ${session.staffName}`;
                }

                return (
                  <button
                    key={session.sessionId}
                    onClick={() => handleSelectSession(session)}
                    className={`flex w-full flex-col gap-1 rounded p-3 text-left transition-colors ${
                      selectedSession === session.sessionId
                        ? "bg-surface-container text-primary border-l-4 border-black font-medium"
                        : "hover:bg-surface-container-low text-secondary"
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-label-sm max-w-37.5 truncate font-semibold">
                        {session.customerName &&
                        session.customerName !== "Khách vãng lai"
                          ? session.customerName
                          : `Session ID: ${session.sessionId.substring(0, 12)}...`}
                      </span>
                      <span
                        className={`rounded-full px-1.5 py-0.5 text-[10px] font-medium ${badgeColor}`}
                      >
                        {badgeText}
                      </span>
                    </div>
                    <p className="mt-1 truncate text-xs text-neutral-500">
                      {session.lastMessage}
                    </p>
                  </button>
                );
              })
            )}
          </div>
        </div>

        <div className="bg-surface-container-lowest border-outline-variant flex flex-1 flex-col overflow-hidden border">
          {selectedSession ? (
            <>
              <div className="border-outline-variant bg-surface-container-low flex items-center justify-between border-b p-4">
                <div>
                  <h3 className="text-body-md text-primary font-bold">
                    Đang hỗ trợ:{" "}
                    {activeSession?.customerName || selectedSession}
                  </h3>
                  <span className="mt-0.5 flex items-center gap-1 text-xs font-medium text-green-600">
                    <span className="h-1.5 w-1.5 animate-ping rounded-full bg-green-500" />{" "}
                    Trực tuyến
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  {!isReadOnly && (
                    <button
                      onClick={() => handleResolveSession(selectedSession)}
                      className="rounded bg-black px-4 py-2 text-xs font-semibold text-white transition-colors hover:bg-neutral-800"
                    >
                      Kết thúc
                    </button>
                  )}
                </div>
              </div>

              <div className="flex-1 space-y-4 overflow-y-auto bg-neutral-50 p-4">
                {isReadOnly && (
                  <div className="mb-4 rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
                    <div className="flex items-center gap-2 font-semibold">
                      <span className="material-symbols-outlined text-[18px]">
                        warning
                      </span>{" "}
                      Chế độ chỉ xem
                    </div>
                    <p className="mt-1 text-xs">
                      Cuộc hội thoại này đang được hỗ trợ bởi{" "}
                      <strong>{activeSession?.staffName}</strong>. Bạn không thể
                      trả lời hoặc giải quyết phiên này.
                    </p>
                  </div>
                )}

                {(messages[selectedSession] || []).length === 0 ? (
                  <div className="text-secondary p-8 text-center text-xs">
                    Bắt đầu cuộc trò chuyện. Hãy gửi lời chào đến khách hàng!
                  </div>
                ) : (
                  (messages[selectedSession] || []).map((msg, index) => {
                    const isStaff = msg.sender === "STAFF";
                    const senderName = getSenderName(
                      msg,
                      activeSession,
                      "Bạn (Nhân viên)",
                    );

                    return (
                      <div
                        key={
                          msg.timestamp ? `${msg.timestamp}-${index}` : index
                        }
                        className={`flex max-w-[70%] flex-col ${isStaff ? "ml-auto items-end" : "mr-auto items-start"}`}
                      >
                        <span className="mr-1 mb-1 ml-1 text-[10px] font-medium text-neutral-400">
                          {senderName}
                        </span>
                        <div
                          className={`rounded-2xl px-4 py-2.5 text-sm leading-relaxed whitespace-pre-wrap shadow-sm ${
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

              <form
                onSubmit={handleSendMessage}
                className="border-outline-variant flex gap-2 border-t p-4"
              >
                <input
                  type="text"
                  value={inputText}
                  onChange={(event) => setInputText(event.target.value)}
                  placeholder={
                    isReadOnly
                      ? "Hội thoại đã bị khóa bởi nhân viên khác..."
                      : "Nhập tin nhắn trả lời khách hàng..."
                  }
                  disabled={isReadOnly}
                  className="border-outline-variant flex-1 rounded border px-4 py-2.5 text-sm focus:border-black focus:outline-none disabled:bg-neutral-50 disabled:text-neutral-400"
                />
                <button
                  type="submit"
                  disabled={isReadOnly || !inputText.trim()}
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
