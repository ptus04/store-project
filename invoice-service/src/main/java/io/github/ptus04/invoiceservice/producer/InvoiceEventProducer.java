package io.github.ptus04.invoiceservice.producer;

import io.github.ptus04.common.event.InvoiceCreatedEvent;
import io.github.ptus04.invoiceservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceEventProducer {
    private final AmqpTemplate amqpTemplate;

    public void publishInvoiceCreatedEvent(InvoiceCreatedEvent invoiceCreatedEvent) {
        log.atInfo()
                .setMessage("Publishing InvoiceCreatedEvent for orderId: {}, orderCode: {}, email: {}")
                .addArgument(invoiceCreatedEvent.orderId())
                .addArgument(invoiceCreatedEvent.orderCode())
                .addArgument(invoiceCreatedEvent.email())
                .log();
        amqpTemplate.convertAndSend(
                RabbitMQConfig.INVOICE_EXCHANGE,
                RabbitMQConfig.INVOICE_CREATED_ROUTING_KEY,
                invoiceCreatedEvent
        );
    }
}
