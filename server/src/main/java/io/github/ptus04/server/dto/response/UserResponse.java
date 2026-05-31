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
                           Instant phoneVerifiedAt,
                           Instant emailVerifiedAt,
                           Instant createdAt,
                           Instant updatedAt,
                           Instant disabledAt) implements Serializable {
    public boolean isPhoneVerified() {
        return phoneVerifiedAt != null;
    }
}