package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendOrderEmail(String email, String invoiceLink) {

    }
}
