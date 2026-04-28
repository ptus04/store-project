package io.github.ptus04.server.service.impl;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.PublicAccessType;
import io.github.ptus04.server.config.storage.StorageProperties;
import io.github.ptus04.server.dto.UserChangePasswordRequest;
import io.github.ptus04.server.dto.UserRegisterRequest;
import io.github.ptus04.server.dto.UserResponse;
import io.github.ptus04.server.dto.UserUpdateRequest;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String AVATAR_CONTAINER = "avatars";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final BlobServiceClient blobServiceClient;
    private final StorageProperties storageProperties;

    @Override
    @Transactional
    public User registerUser(UserRegisterRequest registration) {
        if (userRepository.findByPhone(registration.phone()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký. Vui lòng sử dụng số khác.");
        }
        if (registration.email() != null && !registration.email().isBlank()) {
            if (userRepository.findByEmail(registration.email()).isPresent()) {
                throw new RuntimeException("Email này đã được đăng ký. Vui lòng sử dụng email khác.");
            }
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(registration.name());
        user.setPhone(registration.phone());
        String email = registration.email();
        user.setEmail((email != null && !email.isBlank()) ? email : null);
        user.setPassword(passwordEncoder.encode(registration.password()));
        user.setRole(UserRoleEnum.CUSTOMER);
        return userRepository.save(user);
    }

    @Override
    public UserResponse getUserProfile(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + phone));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String phone, UserUpdateRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + phone));

        if (request.email() != null && !request.email().isBlank()) {
            userRepository.findByEmail(request.email()).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new RuntimeException("Email này đã được sử dụng bởi tài khoản khác.");
                }
            });
        }

        userMapper.partialUpdate(request, user);
        String email = request.email();
        user.setEmail((email != null && !email.isBlank()) ? email : null);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String phone, UserChangePasswordRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + phone));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng.");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận không khớp.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse uploadAvatar(String phone, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ chấp nhận file ảnh (JPEG, PNG, WebP...).");
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + phone));

        String originalFilename = file.getOriginalFilename();
        String ext = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String blobName = user.getId().toString() + "/" + UUID.randomUUID() + ext;

        try {
            var containerClient = blobServiceClient.getBlobContainerClient(AVATAR_CONTAINER);
            if (!containerClient.exists()) {
                containerClient.createWithResponse(null, PublicAccessType.BLOB, null, null);
            }
            var blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            String avatarUrl = storageProperties.getUrl() + "/" + AVATAR_CONTAINER + "/" + blobName;
            user.setAvatar(avatarUrl);
            return userMapper.toResponse(userRepository.save(user));
        } catch (IOException e) {
            throw new RuntimeException("Không thể upload file: " + e.getMessage());
        }
    }
}
