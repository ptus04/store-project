package io.github.ptus04.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link io.github.ptus04.server.entity.User}
 */
public record UserLoginRequest(@NotNull @Size(max = 10) String phone,
                               @NotNull @Size(max = 255) String password) implements Serializable {
}