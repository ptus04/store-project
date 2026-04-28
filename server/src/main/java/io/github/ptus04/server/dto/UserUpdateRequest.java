package io.github.ptus04.server.dto;

import io.github.ptus04.server.enums.UserGenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateRequest(
        @NotBlank(message = "Tên không được để trống")
        @Size(max = 128)
        String name,

        @Email(message = "Email không hợp lệ")
        @Size(max = 64)
        String email,

        UserGenderEnum gender,

        LocalDate birthDate
) {
}
