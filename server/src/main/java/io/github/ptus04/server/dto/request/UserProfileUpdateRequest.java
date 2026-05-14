package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserProfileUpdateRequest(
        @Pattern(regexp = "^0[2-9]\\d{8}$", message = "Định dạng số điện thoại không hợp lệ") String phone,
        @NotBlank(message = "Tên không được để trống") String name,
        @Email(message = "Định dạng email không hợp lệ") String email
) {
}
