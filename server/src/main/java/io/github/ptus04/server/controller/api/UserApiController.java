package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.UserChangePasswordRequest;
import io.github.ptus04.server.dto.UserResponse;
import io.github.ptus04.server.dto.UserUpdateRequest;
import io.github.ptus04.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            UserResponse updated = userService.updateProfile(principal.getName(), request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            Principal principal,
            @Valid @RequestBody UserChangePasswordRequest request
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            userService.changePassword(principal.getName(), request);
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            Principal principal,
            @RequestParam("file") MultipartFile file
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            UserResponse updated = userService.uploadAvatar(principal.getName(), file);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}


