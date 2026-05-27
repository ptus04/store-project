package io.github.ptus04.server.producer;

import io.github.ptus04.common.event.OrderPaidEvent;
import io.github.ptus04.server.config.RabbitMQConfig;
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
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_PAID_ROUTING_KEY,
                orderPaidEvent
        );
    }
}
