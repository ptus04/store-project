package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.exception.ExistedPhoneNumberException;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(RegistrationRequest request) {
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new ExistedPhoneNumberException("Số điện thoại đang được sử dụng");
        }

        User user = new User();
        user.setName(request.name());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRoleEnum.CUSTOMER);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updatePhoneVerificationState(UUID id, boolean isVerified) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        user.setVerifiedAt(isVerified ? Instant.now() : null);
        return userMapper.toUserResponse(userRepository.save(user));
    }

}
