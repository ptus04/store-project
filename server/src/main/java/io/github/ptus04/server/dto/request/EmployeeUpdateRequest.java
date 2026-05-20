package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.enums.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for updating an employee account
 */
public record EmployeeUpdateRequest(
        @Pattern(regexp = "^0[2-9]\\d{8}$", message = "Định dạng số điện thoại không hợp lệ")
        String phone,

        @NotBlank(message = "Tên không được để trống")
        String name,

        @Email(message = "Định dạng email không hợp lệ")
        String email,

        @NotNull(message = "Vai trò không được để trống")
        UserRoleEnum role,

        UserGenderEnum gender,

        LocalDate birthDate
) implements Serializable {
}

