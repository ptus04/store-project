package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.exception.PhoneExistedException;
import io.github.ptus04.server.exception.UserNotFoundException;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    @Override
    public UserResponse updateProfile(UUID id, UserProfileUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (StringUtils.hasText(request.phone()) && !request.phone().equals(user.getPhone())) {
            userRepository.findByPhone(request.phone())
                    .filter(existed -> !existed.getId().equals(id))
                    .ifPresent(existed -> {
                        throw new PhoneExistedException("Số điện thoại đang được sử dụng");
                    });
            user.setPhone(request.phone());
            user.setPhoneVerifiedAt(null);
        }

        user.setName(request.name());
        user.setEmail(StringUtils.hasText(request.email()) ? request.email() : null);
        user.setGender(request.gender());
        user.setBirthDate(request.birthDate());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public long countByRole(UserRoleEnum role) {
        return userRepository.countByRole(role);
    }

    @Override
    public List<UserResponse> getUsersByRole(UserRoleEnum role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public Page<UserResponse> getUsersByRolePaged(UserRoleEnum role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRole(role, pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public Page<UserResponse> getAllUsersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }
}

