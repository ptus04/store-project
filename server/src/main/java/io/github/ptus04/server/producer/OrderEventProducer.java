package io.github.ptus04.server.producer;

import io.github.ptus04.server.config.OrderRabbitMQConfig;
import io.github.ptus04.server.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final AmqpTemplate amqpTemplate;

    public void publishOrderPaidEvent(OrderPaidEvent orderPaidEvent) {
        log.info("Publishing OrderPaidEvent for order code {}", orderPaidEvent.orderCode());
        amqpTemplate.convertAndSend(
                OrderRabbitMQConfig.ORDER_EXCHANGE,
                OrderRabbitMQConfig.ORDER_PAID_ROUTING_KEY,
                orderPaidEvent
        );
    }
}
