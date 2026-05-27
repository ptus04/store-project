package io.github.ptus04.emailservice.service;

public interface EmailService {
    void sendInvoiceEmail(String toEmail, String orderCode, String invoiceLink);
}
