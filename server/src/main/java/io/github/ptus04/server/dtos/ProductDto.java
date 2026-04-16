package io.github.ptus04.server.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entities.Product}
 */
public record ProductDto(@Size(max = 16) UUID id, @NotNull @Size(max = 255) String name, String description,
                         String careInstructions, @NotNull BigDecimal price, @NotNull Integer inStock,
                         @NotNull Boolean isNew, @NotNull Float discount, @NotNull Instant createdAt,
                         @NotNull Instant updatedAt, List<ProductImageDto> productImages) implements Serializable {
}