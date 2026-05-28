package io.github.ptus04.server.chat.producer;

import io.github.ptus04.server.chat.config.ChatRabbitMQConfig;
import io.github.ptus04.server.chat.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventProducer {
    private final AmqpTemplate amqpTemplate;

    public void publishChatMessageEvent(ChatMessageEvent chatMessageEvent) {
        log.info("Publishing ChatMessageEvent for session {}", chatMessageEvent.message().getSessionId());
        amqpTemplate.convertAndSend(
                ChatRabbitMQConfig.CHAT_EXCHANGE,
                ChatRabbitMQConfig.CHAT_MESSAGE_SENT_ROUTING_KEY,
                chatMessageEvent
        );
    }
}
