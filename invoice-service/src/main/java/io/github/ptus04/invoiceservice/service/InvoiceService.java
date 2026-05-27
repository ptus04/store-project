package io.github.ptus04.invoiceservice.service;

import io.github.ptus04.common.event.OrderPaidEvent;

public interface InvoiceService {
    void createInvoice(OrderPaidEvent orderPaidEvent);
}
