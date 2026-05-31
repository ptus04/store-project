package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * DTO for {@link io.github.ptus04.server.entity.User}
 */
public record UserChangePasswordRequest(
        @Pattern(regexp = "0[2-9]\\d{8}",
                message = "Định dạng số điện thoại không hợp lệ")
        String phone,
        @Pattern(regexp = "(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&_]).{8,}",
                message = "Mật khẩu không đúng")
        String password,
        @Pattern(regexp = "\\d{6}",
                message = "Mã OTP không hợp lệ")
        String otp
) implements Serializable {
}