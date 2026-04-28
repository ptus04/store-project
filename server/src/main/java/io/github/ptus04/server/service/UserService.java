package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.UserChangePasswordRequest;
import io.github.ptus04.server.dto.UserRegisterRequest;
import io.github.ptus04.server.dto.UserResponse;
import io.github.ptus04.server.dto.UserUpdateRequest;
import io.github.ptus04.server.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User registerUser(UserRegisterRequest registration);
    UserResponse getUserProfile(String phone);
    UserResponse updateProfile(String phone, UserUpdateRequest request);
    void changePassword(String phone, UserChangePasswordRequest request);
    UserResponse uploadAvatar(String phone, MultipartFile file);
}

