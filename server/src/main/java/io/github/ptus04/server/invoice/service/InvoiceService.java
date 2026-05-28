package io.github.ptus04.server.invoice.service;

import io.github.ptus04.server.event.OrderPaidEvent;

public interface InvoiceService {
    void createInvoice(OrderPaidEvent orderPaidEvent);
}
