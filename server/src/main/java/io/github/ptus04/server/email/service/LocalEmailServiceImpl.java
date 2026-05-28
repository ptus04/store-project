package io.github.ptus04.server.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocalEmailServiceImpl implements EmailService {
    @Override
    public void sendInvoiceEmail(String toEmail, String orderCode, String invoiceLink) {
        log.atInfo()
                .setMessage("Simulating sending email to {} for order {} with invoice link: {}")
                .addArgument(toEmail)
                .addArgument(orderCode)
                .addArgument(invoiceLink)
                .log();
    }

}
