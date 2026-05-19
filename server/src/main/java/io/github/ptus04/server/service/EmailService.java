package io.github.ptus04.server.service;

public interface EmailService {
    //    gửi email thông báo thành công bao gồm thông tin hoa đơn(link)
    void sendOrderEmail(String email, String orderCode, String invoiceLink);
}
