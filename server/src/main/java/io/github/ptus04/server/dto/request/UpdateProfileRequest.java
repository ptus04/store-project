package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        @NotBlank(message = "Tên không được để trống") String name,
        @Pattern(regexp = "^0[2-9]\\d{8}$", message = "Định dạng số điện thoại không hợp lệ") String phone,
        @Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email không hợp lệ") String email
) {
}