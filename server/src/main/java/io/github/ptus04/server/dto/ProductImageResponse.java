package io.github.ptus04.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.ProductImage}
 */
public record ProductImageResponse(UUID id, @NotNull @Size(max = 128) String file,
                                   @NotNull Instant createdAt) implements Serializable {
}