package event;

public record InvoiceCreatedEvent(String email, String orderId, String invoiceLink) {
}
