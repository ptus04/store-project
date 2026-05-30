package io.github.ptus04.server.service;

public interface EmailService {
    void sendInvoiceEmail(String toEmail, String orderCode, String invoiceLink);

    long sendEmailVerificationOtp(String email);

    boolean verifyEmailOtp(String email, String otp);
}
