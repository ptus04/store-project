import { useEffect, useRef, useState } from "react";
import { useSupportChat } from "./useSupportChat";

export default function ChatSupportWidget() {
  const [isOpen, setIsOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  const {
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

    handleSelectSession,
  } = useSupportChat();

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, selectedSession, isOpen]);

  const selectedCustomerLabel = selectedSession
    ? activeSession?.customerName || selectedSession.substring(0, 8)
    : "Hỗ trợ khách hàng";

  return (
    <>
      <button
        onClick={() => setIsOpen((prev) => !prev)}
        className="fixed right-6 bottom-6 z-50 flex h-14 w-14 items-center justify-center rounded-full bg-black text-white shadow-lg transition-transform duration-300 hover:scale-105 focus:outline-none active:scale-95"
        title="Hỗ trợ khách hàng"
      >
        <span className="material-symbols-outlined text-[28px]">
          {isOpen ? "close" : "support_agent"}
        </span>

        {sessions.length > 0 && !isOpen && (
          <span className="absolute -top-1 -right-1 flex h-6 w-6 items-center justify-center rounded-full bg-red-600 text-[11px] font-bold text-white ring-2 ring-white">
            {sessions.length}
            <span className="absolute -z-10 h-full w-full animate-ping rounded-full bg-red-600 opacity-75" />
          </span>
        )}
      </button>

      {isOpen && (
        <div className="fixed right-6 bottom-24 z-50 flex h-[560px] w-[380px] max-w-[90vw] flex-col rounded-2xl border border-neutral-200 bg-white shadow-2xl transition-all duration-300 md:w-[420px]">
          <div className="flex items-center justify-between rounded-t-2xl border-b border-neutral-100 bg-neutral-900 px-4 py-3.5 text-white">
            <div className="flex items-center gap-2">
              {selectedSession && (
                <button
                  onClick={() => setSelectedSession(null)}
                  className="mr-1 flex items-center justify-center text-neutral-300 transition-colors hover:text-white"
                >
                  <span className="material-symbols-outlined text-[20px]">
                    arrow_back
                  </span>
                </button>
              )}
              <div>
                <h3 className="text-sm leading-tight font-bold">
                  {selectedSession
                    ? `Hỗ trợ: ${selectedCustomerLabel}`
                    : "Hỗ trợ khách hàng"}
                </h3>
                <div className="mt-0.5 flex items-center gap-1">
                  <span
                    className={`inline-block h-1.5 w-1.5 rounded-full ${
                      connected ? "animate-pulse bg-green-500" : "bg-red-500"
                    }`}
                  />
                  <span className="text-[10px] font-medium text-neutral-300">
                    {connected ? "Realtime" : "Mất kết nối"}
                  </span>
                </div>
              </div>
            </div>

            {selectedSession && (
              <div className="flex items-center gap-2">
                {!isReadOnly && (
                  <button
                    onClick={() => handleResolveSession(selectedSession)}
                    className="rounded-lg bg-red-600/90 px-2.5 py-1 text-[11px] font-bold text-white transition-colors hover:bg-red-600"
                  >
                    Kết thúc
                  </button>
                )}
              </div>
            )}
          </div>

          <div className="flex flex-1 flex-col overflow-hidden rounded-b-2xl bg-neutral-50">
            {selectedSession ? (
              <>
                <div className="flex-1 space-y-3 overflow-y-auto p-4">
                  {isReadOnly && (
                    <div className="mb-3 rounded-xl border border-amber-200 bg-amber-50 p-3 text-xs text-amber-800">
                      <div className="flex items-center gap-1.5 font-semibold">
                        <span className="material-symbols-outlined text-[16px]">
                          warning
                        </span>
                        Chế độ chỉ xem
                      </div>
                      <p className="mt-1 text-[10px] leading-relaxed">
                        Phiên hỗ trợ này đã được nhận bởi{" "}
                        <strong>{activeSession?.staffName}</strong>. Bạn không
                        thể gửi tin nhắn hoặc đóng cuộc hội thoại này.
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

                      return (
                        <div
                          key={index}
                          className={`flex max-w-[85%] flex-col ${
                            isStaff
                              ? "ml-auto items-end"
                              : "mr-auto items-start"
                          }`}
                        >
                          <span className="mr-1 mb-0.5 ml-1 text-[9px] font-medium text-neutral-400">
                            {isStaff ? "Bạn" : "Khách hàng"}
                          </span>
                          <div
                            className={`rounded-2xl px-3.5 py-2 text-xs leading-relaxed shadow-sm ${
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
                  className="flex gap-2 border-t border-neutral-200 bg-white p-3"
                >
                  <input
                    type="text"
                    value={inputText}
                    onChange={(event) => setInputText(event.target.value)}
                    placeholder={
                      isReadOnly
                        ? "Hội thoại đã bị khóa bởi nhân viên khác..."
                        : "Nhập tin nhắn..."
                    }
                    disabled={isReadOnly}
                    className="flex-1 rounded-xl border border-neutral-200 px-3.5 py-2 text-xs focus:border-black focus:outline-none disabled:bg-neutral-50 disabled:text-neutral-400"
                  />
                  <button
                    type="submit"
                    disabled={isReadOnly || !inputText.trim()}
                    className="flex items-center justify-center rounded-xl bg-black px-4 py-2 text-xs font-semibold text-white transition-all hover:bg-neutral-800 disabled:opacity-55"
                  >
                    <span className="material-symbols-outlined mr-1 text-[16px]">
                      send
                    </span>
                    Gửi
                  </button>
                </form>
              </>
            ) : (
              <div className="flex flex-1 flex-col overflow-hidden">
                <div className="border-b border-neutral-100 bg-white p-3">
                  <h4 className="text-xs font-bold text-neutral-800">
                    Khách hàng đang trực tuyến ({sessions.length})
                  </h4>
                </div>

                <div className="flex-1 space-y-1 overflow-y-auto p-2">
                  {sessions.length === 0 ? (
                    <div className="flex h-full flex-col items-center justify-center p-6 text-center">
                      <span className="material-symbols-outlined text-[44px] text-neutral-300">
                        chat_bubble_outline
                      </span>
                      <p className="mt-2 text-xs font-medium text-neutral-700">
                        Không có yêu cầu hỗ trợ nào
                      </p>
                      <p className="mt-0.5 max-w-[200px] text-[10px] text-neutral-400">
                        Khi có khách hàng cần hỗ trợ, họ sẽ xuất hiện tại đây.
                      </p>
                    </div>
                  ) : (
                    sessions.map((session) => {
                      const isClaimedByMe =
                        session.staffName === currentStaffName;
                      const isClaimedByOther =
                        session.staffName && !isClaimedByMe;

                      let badgeColor =
                        "bg-emerald-50 text-emerald-700 animate-pulse";
                      let badgeText = "Chưa nhận";

                      if (isClaimedByMe) {
                        badgeColor = "bg-blue-50 text-blue-700 font-bold";
                        badgeText = "Bạn hỗ trợ";
                      } else if (isClaimedByOther) {
                        badgeColor = "bg-purple-50 text-purple-700";
                        badgeText = `Hỗ trợ: ${session.staffName}`;
                      }

                      return (
                        <button
                          key={session.sessionId}
                          onClick={() => handleSelectSession(session)}
                          className="flex w-full flex-col gap-1 rounded-xl border border-neutral-100 bg-white p-3 text-left shadow-sm transition-all hover:border-neutral-300 hover:shadow"
                        >
                          <div className="flex items-center justify-between">
                            <span className="max-w-[180px] truncate text-[11px] font-bold text-neutral-800">
                              {session.customerName &&
                              session.customerName !== "Khách vãng lai"
                                ? session.customerName
                                : `Session: #${session.sessionId.substring(0, 8)}`}
                            </span>
                            <span
                              className={`rounded-full px-2 py-0.5 text-[9px] font-bold ${badgeColor}`}
                            >
                              {badgeText}
                            </span>
                          </div>
                          <p className="mt-0.5 truncate text-[11px] text-neutral-500">
                            {session.lastMessage}
                          </p>
                        </button>
                      );
                    })
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
}
