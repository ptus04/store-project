package io.github.ptus04.emailservice.consumer;

import event.InvoiceCreatedEvent;
import io.github.ptus04.emailservice.config.RabbitMQConfig;
import io.github.ptus04.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.INVOICE_QUEUE)
    public void consumeInvoiceCreatedEvent(InvoiceCreatedEvent invoiceCreatedEvent) {
        emailService.sendInvoiceEmail(
                invoiceCreatedEvent.email(),
                invoiceCreatedEvent.orderId(),
                invoiceCreatedEvent.invoiceLink()
        );
    }
}
