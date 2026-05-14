package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link io.github.ptus04.server.entity.User}
 */
public record RegistrationRequest(
        @Pattern(regexp = "^0[2-9]\\d{8}$",
                message = "Định dạng số điện thoại không hợp lệ") String phone,
        @NotBlank(message = "Tên không được để trống") String name,
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&_]).{8,}$",
                message = "Mật khẩu phải có ít nhất 8 ký tự và chứa ít nhất một chữ cái, một số và một ký tự đặc biệt")
        String password) {
}
