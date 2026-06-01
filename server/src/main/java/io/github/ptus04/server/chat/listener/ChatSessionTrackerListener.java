package io.github.ptus04.server.chat.listener;

import io.github.ptus04.server.dto.ChatMessageDto;
import io.github.ptus04.server.dto.SupportSessionDto;
import io.github.ptus04.server.chat.event.LocalChatMessageReceivedEvent;
import io.github.ptus04.server.service.impl.ChatSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSessionTrackerListener {
    private final ChatSessionManager chatSessionManager;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void trackSession(LocalChatMessageReceivedEvent event) {
        ChatMessageDto message = event.message();
        if (message.getSessionId() == null) {
            return;
        }

        log.info("Observer tracking support session for ID: {}", message.getSessionId());
        SupportSessionDto session = chatSessionManager.getSession(message.getSessionId());

        if ("USER".equalsIgnoreCase(message.getSender())) {
            if (session == null) {
                session = new SupportSessionDto(message.getSessionId(), true, message.getContent(), "Khách hàng", null);
                chatSessionManager.putSession(message.getSessionId(), session);
            } else {
                if (!session.isActive()) {
                    session.setActive(true);
                    session.setStaffName(null);
                }
                session.setLastMessage(message.getContent());
            }
            messagingTemplate.convertAndSend("/topic/support/requests", session);
        } else {
            if (session != null && session.isActive()) {
                session.setLastMessage(message.getContent());
                messagingTemplate.convertAndSend("/topic/support/requests", session);
            }
        }
    }
}
