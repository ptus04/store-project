package io.github.ptus04.server.service;

public interface SMSVerificationService {

    long sendOtp(String phone);

    boolean verifyOtp(String phone, String otp);

}
