package io.github.ptus04.common.event;

public record InvoiceCreatedEvent(
        String email,
        String orderId,
        String orderCode,
        String invoiceLink) {
}
