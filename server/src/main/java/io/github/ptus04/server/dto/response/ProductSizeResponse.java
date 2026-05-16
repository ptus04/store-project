package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.ProductSize}
 */
public record ProductSizeResponse(UUID id,
                                  String name,
                                  Integer inStock,
                                  Instant createdAt,
                                  Instant updatedAt) implements Serializable {
    public boolean isOutOfStock() {
        return inStock == null || inStock <= 0;
    }
}
