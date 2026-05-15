package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.exception.PhoneExistedException;
import io.github.ptus04.server.exception.UserNotFoundException;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
}
