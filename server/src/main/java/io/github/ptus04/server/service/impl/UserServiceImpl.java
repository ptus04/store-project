package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.EmployeeCreateRequest;
import io.github.ptus04.server.dto.request.EmployeeUpdateRequest;
import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.exception.BusinessConstraintViolationException;
import io.github.ptus04.server.exception.PhoneExistedException;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public UserResponse updateProfile(UUID id, UserProfileUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

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

    @Override
    public UserResponse createEmployee(UUID actorId, EmployeeCreateRequest request) {
        User actor = userRepository.findById(actorId).orElseThrow(EntityNotFoundException::new);
        if (actor.getRole() != UserRoleEnum.ADMIN) {
            throw new BusinessConstraintViolationException("Bạn không có quyền tạo tài khoản");
        }

        // Kiểm tra số điện thoại đã tồn tại
        userRepository.findByPhone(request.phone()).ifPresent(existed -> {
            throw new PhoneExistedException("Số điện thoại đang được sử dụng");
        });

        // Tạo user mới
        User newUser = new User();
        newUser.setPhone(request.phone());
        newUser.setName(request.name());
        newUser.setEmail(StringUtils.hasText(request.email()) ? request.email() : null);
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        newUser.setGender(request.gender());
        newUser.setBirthDate(request.birthDate());
        newUser.setCreatedAt(Instant.now());
        newUser.setUpdatedAt(Instant.now());
        newUser.setDisabledAt(null);

        User savedUser = userRepository.save(newUser);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse updateEmployee(UUID actorId, UUID targetId, EmployeeUpdateRequest request) {
        User actor = userRepository.findById(actorId).orElseThrow(EntityNotFoundException::new);
        if (actor.getRole() != UserRoleEnum.ADMIN) {
            throw new BusinessConstraintViolationException("Bạn không có quyền cập nhật tài khoản");
        }

        User target = userRepository.findById(targetId).orElseThrow(EntityNotFoundException::new);

        // Kiểm tra nếu thay đổi số điện thoại
        if (StringUtils.hasText(request.phone()) && !request.phone().equals(target.getPhone())) {
            userRepository.findByPhone(request.phone())
                    .filter(existed -> !existed.getId().equals(targetId))
                    .ifPresent(existed -> {
                        throw new PhoneExistedException("Số điện thoại đang được sử dụng");
                    });
            target.setPhone(request.phone());
            target.setPhoneVerifiedAt(null);
        }

        target.setName(request.name());
        target.setEmail(StringUtils.hasText(request.email()) ? request.email() : null);
        target.setRole(request.role());
        target.setGender(request.gender());
        target.setBirthDate(request.birthDate());
        target.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(target);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse updateEmployeeAccountStatus(UUID actorId, UUID targetId, boolean disabled) {
        User actor = userRepository.findById(actorId).orElseThrow(EntityNotFoundException::new);
        if (actor.getRole() != UserRoleEnum.ADMIN) {
            throw new BusinessConstraintViolationException("Bạn không có quyền vô hiệu hóa tài khoản");
        }

        User target = userRepository.findById(targetId).orElseThrow(EntityNotFoundException::new);
        if (actorId.equals(targetId)) {
            throw new BusinessConstraintViolationException("Không thể tự vô hiệu hóa tài khoản của chính mình");
        }

        target.setDisabledAt(disabled ? Instant.now() : null);
        User savedUser = userRepository.save(target);
        return userMapper.toUserResponse(savedUser);
    }
}

