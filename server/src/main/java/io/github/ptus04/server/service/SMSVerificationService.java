package io.github.ptus04.server.service;

public interface SMSVerificationService {
    String sendVerificationCode(String phoneNumber);

    boolean verifyCode(String phoneNumber, String verificationCode);
}
