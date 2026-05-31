package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.UserChangePasswordRequest;
import io.github.ptus04.server.dto.request.UserRegistrationRequest;
import io.github.ptus04.server.dto.response.PhoneVerificationResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.exception.PhoneExistedException;
import io.github.ptus04.server.exception.UserPhoneVerifiedException;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.AuthService;
import io.github.ptus04.server.service.SMSVerificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final SMSVerificationService smsVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Authentication register(UserRegistrationRequest request) {
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new PhoneExistedException("Số điện thoại đang được sử dụng");
        }

        User user = new User();
        user.setName(request.name());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRoleEnum.CUSTOMER);
        user = userRepository.save(user);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getPhone(), request.password()));
    }

    @Override
    public PhoneVerificationResponse sendPhoneVerification(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if (user.isPhoneVerified()) {
            throw new UserPhoneVerifiedException(user.getPhone() + " is already verified");
        }

        long remainingTime = smsVerificationService.sendOtp(user.getPhone());

        return new PhoneVerificationResponse(user.getId(), user.getPhone(), remainingTime);
    }

    @Override
    public long sendPhoneOtp(String phone) {
        User user = userRepository.findByPhone(phone).orElseThrow(EntityNotFoundException::new);
        return smsVerificationService.sendOtp(user.getPhone());
    }

    @Override
    public boolean verifyOtp(UUID userId, String otp) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        boolean isSuccess = smsVerificationService.verifyOtp(user.getPhone(), otp);
        if (!isSuccess) {
            return false;
        }

        user.setPhoneVerifiedAt(Instant.now());
        user = userRepository.save(user);

        return user.isPhoneVerified();
    }

    @Override
    public boolean changePassword(UserChangePasswordRequest request) {
        boolean result = smsVerificationService.verifyOtp(request.phone(), request.otp());
        if (!result) return false;

        User user = userRepository.findByPhone(request.phone()).orElseThrow(EntityNotFoundException::new);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return true;
    }
}
