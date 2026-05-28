package io.github.ptus04.server.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitMQConfig {
    public static final String ORDER_EXCHANGE = "orders.topic";
    public static final String ORDER_PAID_ROUTING_KEY = "order.paid";
}
