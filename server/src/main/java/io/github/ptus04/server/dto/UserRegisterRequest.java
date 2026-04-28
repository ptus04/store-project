package io.github.ptus04.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "Tên không được để trống")
        @Size(max = 128)
        String name,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ (10 chữ số bắt đầu bằng 0)")
        String phone,

        @Email(message = "Email không hợp lệ")
        @Size(max = 64)
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&_]).{8,}$",
                message = "Mật khẩu phải từ 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
        String password
) {
}
