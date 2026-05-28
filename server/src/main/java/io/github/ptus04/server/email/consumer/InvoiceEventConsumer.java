package io.github.ptus04.server.email.consumer;

import io.github.ptus04.server.config.RabbitMQConfig;
import io.github.ptus04.server.email.service.EmailService;
import io.github.ptus04.server.event.InvoiceCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceEventConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SEND_INVOICE)
    public void handleSendInvoiceEvent(InvoiceCreatedEvent invoiceCreatedEvent) {
        emailService.sendInvoiceEmail(
                invoiceCreatedEvent.email(),
                invoiceCreatedEvent.orderCode(),
                invoiceCreatedEvent.invoiceLink()
        );
    }
}
