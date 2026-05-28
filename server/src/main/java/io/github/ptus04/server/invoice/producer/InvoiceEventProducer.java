package io.github.ptus04.server.invoice.producer;

import io.github.ptus04.server.config.RabbitMQConfig;
import io.github.ptus04.server.event.InvoiceCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceEventProducer {
    private final AmqpTemplate amqpTemplate;

    public void publishInvoiceCreatedEvent(InvoiceCreatedEvent invoiceCreatedEvent) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_INVOICES,
                RabbitMQConfig.KEY_INVOICE_CREATED,
                invoiceCreatedEvent
        );
    }
}
