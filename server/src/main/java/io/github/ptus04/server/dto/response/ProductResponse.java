package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.Product}
 */
public record ProductResponse(UUID id,
                              String name,
                              String description,
                              String careInstructions,
                              BigDecimal price,
                              Integer inStock,
                              Boolean isNew,
                              Float discount,
                              Instant createdAt,
                              Instant updatedAt,
                              List<ProductImageResponse> productImages) implements Serializable {
}