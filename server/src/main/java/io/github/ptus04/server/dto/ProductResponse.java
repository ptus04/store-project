package io.github.ptus04.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.Product}
 */
public record ProductResponse(@Size(max = 16) UUID id, @NotNull @Size(max = 255) String name, String description,
                              String careInstructions, @NotNull BigDecimal price, @NotNull Integer inStock,
                              @NotNull Boolean isNew, @NotNull Float discount, @NotNull Instant createdAt,
                              @NotNull Instant updatedAt, List<ProductImageResponse> productImages,
                              List<ProductSizeResponse> productSizes) implements Serializable {
}