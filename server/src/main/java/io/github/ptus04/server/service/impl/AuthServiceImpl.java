package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.exception.VerifiedUserPhoneException;
import io.github.ptus04.server.service.AuthService;
import io.github.ptus04.server.service.SMSVerificationService;
import io.github.ptus04.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SMSVerificationService smsVerificationService;

    @Override
    public void register(HttpSession httpSession,
                         RegistrationRequest registrationRequest) {
        UserResponse user = userService.createUser(registrationRequest);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.phone(), registrationRequest.password());
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        httpSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
    }

    @Override
    public long sendPhoneOtp(UUID userId) {
        UserResponse user = userService.getUserById(userId);
        if (user.isPhoneVerified()) {
            throw new VerifiedUserPhoneException(user.phone() + " is already verified");
        }

        return smsVerificationService.sendOtp(user.phone());
    }

    @Override
    public boolean verifyOtp(UUID userId, String otp) {
        UserResponse user = userService.getUserById(userId);
        if (user.isPhoneVerified()) {
            throw new VerifiedUserPhoneException(user.phone() + " is already verified");
        }

        boolean result = smsVerificationService.verifyOtp(user.phone(), otp);
        if (!result) {
            return false;
        }

        userService.updatePhoneVerificationState(userId, true);

        return true;
    }
}
