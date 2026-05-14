package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);

    UserResponse updateProfile(UUID id, UserProfileUpdateRequest request);
}
