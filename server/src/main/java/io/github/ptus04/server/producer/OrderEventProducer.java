package io.github.ptus04.server.producer;

import io.github.ptus04.server.config.RabbitMQConfig;
import io.github.ptus04.server.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final AmqpTemplate amqpTemplate;

    public void publishOrderPaidEvent(OrderPaidEvent orderPaidEvent) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ORDERS,
                RabbitMQConfig.KEY_ORDER_PAID,
                orderPaidEvent
        );
    }
}
