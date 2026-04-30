package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(RegistrationRequest request);

    UserResponse getUserById(UUID id);

    UserResponse updatePhoneVerificationState(UUID id, boolean isVerified);

    UserResponse updateProfile(UUID id, String name, String email, String avatar);
}

