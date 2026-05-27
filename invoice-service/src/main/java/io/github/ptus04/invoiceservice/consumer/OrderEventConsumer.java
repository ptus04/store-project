package io.github.ptus04.invoiceservice.consumer;

import io.github.ptus04.common.event.OrderPaidEvent;
import io.github.ptus04.invoiceservice.config.RabbitMQConfig;
import io.github.ptus04.invoiceservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final InvoiceService invoiceService;

    @RabbitListener(queues = RabbitMQConfig.CREATE_INVOICE_QUEUE)
    public void createInvoice(OrderPaidEvent orderPaidEvent) {
        log.atInfo().log("Received OrderPaidEvent: {}", orderPaidEvent);
        invoiceService.createInvoice(orderPaidEvent);
    }
}
