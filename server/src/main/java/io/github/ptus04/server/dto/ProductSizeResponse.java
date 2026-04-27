package io.github.ptus04.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.ProductSize}
 */
public record ProductSizeResponse(UUID id, @NotNull @Size(max = 4) String name,
                                   @NotNull Instant createdAt) implements Serializable {
}
