package io.github.ptus04.server.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entities.ProductImage}
 */
public record ProductImageDto(UUID id, @NotNull @Size(max = 128) String file,
                              @NotNull Instant createdAt) implements Serializable {
}