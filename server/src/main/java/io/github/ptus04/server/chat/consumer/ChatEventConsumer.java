package io.github.ptus04.server.chat.consumer;

import io.github.ptus04.server.chat.config.ChatRabbitMQConfig;
import io.github.ptus04.server.chat.controller.ChatSupportController;
import io.github.ptus04.server.chat.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventConsumer {
    private final ChatSupportController chatSupportController;

    @RabbitListener(queues = ChatRabbitMQConfig.CHAT_MESSAGE_QUEUE)
    public void consumeChatMessage(ChatMessageEvent event) {
        log.info("Received ChatMessageEvent via RabbitMQ for session: {}", event.message().getSessionId());
        chatSupportController.processIncomingMessage(event.message());
    }
}
