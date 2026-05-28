package io.github.ptus04.server.email.consumer;

import io.github.ptus04.server.email.config.EmailRabbitMQConfig;
import io.github.ptus04.server.email.service.EmailService;
import io.github.ptus04.server.event.InvoiceCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceEventConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = EmailRabbitMQConfig.SEND_INVOICE_QUEUE)
    public void consumeInvoiceCreatedEvent(InvoiceCreatedEvent invoiceCreatedEvent) {
        emailService.sendInvoiceEmail(
                invoiceCreatedEvent.email(),
                invoiceCreatedEvent.orderId(),
                invoiceCreatedEvent.invoiceLink()
        );
    }
}
