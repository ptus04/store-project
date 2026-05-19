package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse user = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mật khẩu cũ không chính xác"));
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

    public record ChangePasswordRequest(
            @NotBlank(message = "Mật khẩu cũ không được để trống")
            String oldPassword,

            @NotBlank(message = "Mật khẩu mới không được để trống")
            @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
            String newPassword
    ) {}
}
