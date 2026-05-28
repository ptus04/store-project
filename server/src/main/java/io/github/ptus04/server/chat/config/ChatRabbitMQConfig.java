package io.github.ptus04.server.chat.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatRabbitMQConfig {
    public static final String CHAT_MESSAGE_QUEUE = "chat.message-queue";
    public static final String CHAT_EXCHANGE = "chat.topic";
    public static final String CHAT_MESSAGE_SENT_ROUTING_KEY = "chat.message.sent";

    @Bean
    public Queue chatMessageQueue() {
        return QueueBuilder.durable(CHAT_MESSAGE_QUEUE).build();
    }

    @Bean
    public TopicExchange chatExchange() {
        return ExchangeBuilder.topicExchange(CHAT_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindingChatMessage(Queue chatMessageQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatMessageQueue).to(chatExchange).with(CHAT_MESSAGE_SENT_ROUTING_KEY);
    }
}
