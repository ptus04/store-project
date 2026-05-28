package io.github.ptus04.server.invoice.producer;

import io.github.ptus04.server.event.InvoiceCreatedEvent;
import io.github.ptus04.server.invoice.config.InvoiceRabbitMQConfig;
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
                InvoiceRabbitMQConfig.INVOICE_EXCHANGE,
                InvoiceRabbitMQConfig.INVOICE_CREATED_ROUTING_KEY,
                invoiceCreatedEvent
        );
    }
}
