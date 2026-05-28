package io.github.ptus04.server.email.service;

public interface EmailService {
    void sendInvoiceEmail(String toEmail, String orderCode, String invoiceLink);
}
