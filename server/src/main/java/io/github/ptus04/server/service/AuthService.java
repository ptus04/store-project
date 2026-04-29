package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public interface AuthService {
    void register(HttpSession httpSession, RegistrationRequest registrationRequest);

    long sendPhoneOtp(UUID userId);

    boolean verifyOtp(UUID userId, String otp);
}
