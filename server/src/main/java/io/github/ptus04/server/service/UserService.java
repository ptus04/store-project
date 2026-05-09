package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.dto.request.UpdateProfileRequest;
import io.github.ptus04.server.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(RegistrationRequest request);

    UserResponse getUserById(UUID id);

    UserResponse updateProfile(UUID id, UpdateProfileRequest request);

    UserResponse updatePhoneVerificationState(UUID id, boolean isVerified);
}
