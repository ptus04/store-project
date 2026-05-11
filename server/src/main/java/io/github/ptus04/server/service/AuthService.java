package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {
    Authentication register(RegistrationRequest registrationRequest);

    long sendPhoneOtp(UUID userId);

    boolean verifyOtp(UUID userId, String otp);
}
