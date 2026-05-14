package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.dto.request.UpdateProfileRequest;
import io.github.ptus04.server.entity.User;

import java.util.UUID;

public interface UserService {
    User createUser(RegistrationRequest request);

    User getUserById(UUID id);

    User updateProfile(UUID id, UpdateProfileRequest request);

    User updatePhoneVerificationState(UUID id, boolean isVerified);
}
