package io.github.ptus04.server.dto;

import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.enums.UserRoleEnum;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String phone,
        String email,
        UserRoleEnum role,
        UserGenderEnum gender,
        LocalDate birthDate,
        String avatar
) {
}
