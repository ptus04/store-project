package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.enums.UserGenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

import java.io.Serializable;

/**
 * DTO for {@link io.github.ptus04.server.entity.User}
 */
public record UserProfileUpdateRequest(
        @Pattern(regexp = "^0[2-9]\\d{8}$", message = "Định dạng số điện thoại không hợp lệ") String phone,
        @NotBlank(message = "Tên không được để trống") String name,
        @Email(message = "Định dạng email không hợp lệ") String email,
        UserGenderEnum gender,
        @Past(message = "Ngày sinh phải là một ngày trong quá khứ") LocalDate birthDate
) implements Serializable {
}
