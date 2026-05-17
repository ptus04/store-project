package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.UserRoleEnum;

import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID id);

    UserResponse updateProfile(UUID id, UserProfileUpdateRequest request);

    long countByRole(UserRoleEnum role);
}
