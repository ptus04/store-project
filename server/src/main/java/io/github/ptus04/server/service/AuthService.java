package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.ChangePasswordRequest;
import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.dto.response.PhoneVerificationResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {
    Authentication register(RegistrationRequest registrationRequest);

    PhoneVerificationResponse sendPhoneVerification(UUID userId);

    long sendPhoneOtp(String phone);

    boolean verifyOtp(UUID userId, String otp);

    boolean changePassword(ChangePasswordRequest changePasswordRequest);
}
