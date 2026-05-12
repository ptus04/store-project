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
                              Boolean isNew,
                              Float discount,
                              Instant createdAt,
                              Instant updatedAt,
                              List<ProductImageResponse> productImages,
                              List<ProductSizeResponse> productSizes) implements Serializable {
    public int inStock() {
        if (productSizes == null || productSizes.isEmpty()) {
            return 0;
        }

        return productSizes.stream()
                .map(ProductSizeResponse::inStock)
                .filter(stock -> stock != null && stock > 0)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public boolean isOutOfStock() {
        return productSizes == null || productSizes.isEmpty() || productSizes.stream().allMatch(ProductSizeResponse::isOutOfStock);
    }
}