package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.EmployeeCreateRequest;
import io.github.ptus04.server.dto.request.EmployeeUpdateRequest;
import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.email.service.EmailService;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserGenderEnum;
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
    private final EmailService emailService;

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public UserResponse updateProfile(UUID id, UserProfileUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        String currentEmail = normalizeEmail(user.getEmail());
        String requestedEmail = normalizeEmail(request.email());

        validateAndSetPhone(user, request.phone(), id);

        user.setName(request.name());
        user.setEmail(requestedEmail);
        if (!emailsEqual(currentEmail, requestedEmail)) {
            validateEmailAvailable(requestedEmail, id);
            user.setEmailVerifiedAt(null);
            if (StringUtils.hasText(requestedEmail)) {
                emailService.sendEmailVerificationOtp(requestedEmail);
            }
        }
        user.setGender(request.gender());
        user.setBirthDate(request.birthDate());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse verifyProfileEmail(UUID id, String otp) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!StringUtils.hasText(user.getEmail())) {
            throw new BusinessConstraintViolationException("Tài khoản chưa có email");
        }

        boolean verified = emailService.verifyEmailOtp(user.getEmail(), otp);
        if (!verified) {
            throw new BusinessConstraintViolationException("Mã OTP email không chính xác hoặc đã hết hạn");
        }

        user.setEmailVerifiedAt(Instant.now());
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

        userRepository.findByPhone(request.phone()).ifPresent(existed -> {
            throw new PhoneExistedException("Số điện thoại đang được sử dụng");
        });

        User newUser = new User();
        UserResponse mockResponse = new UserResponse(
                null,
                request.name(),
                request.phone(),
                StringUtils.hasText(request.email()) ? request.email() : null,
                request.role(),
                request.gender(),
                request.birthDate(),
                null, null, null, null, null
        );
        userMapper.partialUpdate(mockResponse, newUser);

        newUser.setPassword(passwordEncoder.encode(request.password()));
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

        validateAndSetPhone(target, request.phone(), targetId);

        UserResponse mockUpdateResponse = new UserResponse(
                targetId,
                request.name(),
                request.phone(),
                StringUtils.hasText(request.email()) ? request.email() : null,
                request.role(),
                request.gender(),
                request.birthDate(),
                target.getPhoneVerifiedAt(),
                target.getEmailVerifiedAt(),
                target.getCreatedAt(),
                null,
                target.getDisabledAt()
        );

        userMapper.partialUpdate(mockUpdateResponse, target);
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

    @Override
    public Page<UserResponse> searchCustomers(UserGenderEnum gender, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String keyword = StringUtils.hasText(search) ? search.trim() : null;
        UserGenderEnum genderFilter = gender;
        return userRepository.searchByRoleAndFilters(UserRoleEnum.CUSTOMER, genderFilter, keyword, pageable)
                .map(userMapper::toUserResponse);
    }

    private void validateAndSetPhone(User user, String newPhone, UUID userId) {
        if (StringUtils.hasText(newPhone) && !newPhone.equals(user.getPhone())) {
            userRepository.findByPhone(newPhone)
                    .filter(existed -> !existed.getId().equals(userId))
                    .ifPresent(existed -> {
                        throw new PhoneExistedException("Số điện thoại đang được sử dụng");
                    });
            user.setPhone(newPhone);
            user.setPhoneVerifiedAt(null);
        }
    }

    private String normalizeEmail(String email) {
        return StringUtils.hasText(email) ? email.trim().toLowerCase() : null;
    }

    private boolean emailsEqual(String currentEmail, String requestedEmail) {
        if (currentEmail == null) {
            return requestedEmail == null;
        }
        return currentEmail.equals(requestedEmail);
    }

    private void validateEmailAvailable(String email, UUID userId) {
        if (!StringUtils.hasText(email)) {
            return;
        }

        userRepository.findByEmail(email)
                .filter(existed -> !existed.getId().equals(userId))
                .ifPresent(existed -> {
                    throw new BusinessConstraintViolationException("Email đang được sử dụng");
                });
    }
}
