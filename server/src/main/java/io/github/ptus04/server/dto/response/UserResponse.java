package io.github.ptus04.server.dto.response;

import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.enums.UserRoleEnum;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.User}
 */
public record UserResponse(UUID id,
                           String name,
                           String phone,
                           String email,
                           UserRoleEnum role,
                           UserGenderEnum gender,
                           LocalDate birthDate,
                           Instant createdAt,
                           Instant updatedAt,
                           Instant verifiedAt,
                           String avatar) implements Serializable {
    public boolean isPhoneVerified() {
        return verifiedAt != null;
    }
}