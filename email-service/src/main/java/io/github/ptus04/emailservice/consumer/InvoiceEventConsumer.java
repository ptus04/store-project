package io.github.ptus04.emailservice.consumer;

import io.github.ptus04.common.event.InvoiceCreatedEvent;
import io.github.ptus04.emailservice.config.RabbitMQConfig;
import io.github.ptus04.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceEventConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.SEND_INVOICE_QUEUE)
    public void consumeInvoiceCreatedEvent(InvoiceCreatedEvent invoiceCreatedEvent) {
        emailService.sendInvoiceEmail(
                invoiceCreatedEvent.email(),
                invoiceCreatedEvent.orderId(),
                invoiceCreatedEvent.invoiceLink()
        );
    }
}
