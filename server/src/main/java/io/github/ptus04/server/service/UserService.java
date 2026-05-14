package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID id);
}
