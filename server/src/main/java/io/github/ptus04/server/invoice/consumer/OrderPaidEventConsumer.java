package io.github.ptus04.server.invoice.consumer;

import io.github.ptus04.server.config.RabbitMQConfig;
import io.github.ptus04.server.event.OrderPaidEvent;
import io.github.ptus04.server.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPaidEventConsumer {
    private final InvoiceService invoiceService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CREATE_INVOICE)
    public void handleCreateInvoiceEvent(OrderPaidEvent orderPaidEvent) {
        invoiceService.createInvoice(orderPaidEvent);
    }
}
