package io.github.ptus04.server.event;

public record InvoiceCreatedEvent(String email, String orderCode, String invoiceLink) {
}
