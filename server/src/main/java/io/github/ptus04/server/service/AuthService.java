package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.UserChangePasswordRequest;
import io.github.ptus04.server.dto.request.UserRegistrationRequest;
import io.github.ptus04.server.dto.response.PhoneVerificationResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {
    Authentication register(UserRegistrationRequest userRegistrationRequest);

    PhoneVerificationResponse sendPhoneVerification(UUID userId);

    long sendPhoneOtp(String phone);

    boolean verifyOtp(UUID userId, String otp);

    boolean changePassword(UserChangePasswordRequest userChangePasswordRequest);
}
