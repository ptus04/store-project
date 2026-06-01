package io.github.ptus04.server.chat.listener;

import io.github.ptus04.server.dto.ChatMessageDto;
import io.github.ptus04.server.chat.event.LocalChatMessageReceivedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageWebSocketListener {
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void routeMessage(LocalChatMessageReceivedEvent event) {
        ChatMessageDto message = event.message();
        if (message.getSessionId() != null) {
            log.info("Observer routing message via WebSocket to topic: /topic/chat/{}", message.getSessionId());
            messagingTemplate.convertAndSend("/topic/chat/" + message.getSessionId(), message);
        }
    }
}
