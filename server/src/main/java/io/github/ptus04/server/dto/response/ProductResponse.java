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
                              Float discount,
                              BigDecimal priceDiscount,
                              Instant createdAt,
                              Instant updatedAt,
                              Instant deletedAt,
                              List<ProductImageResponse> productImages,
                              List<ProductSizeResponse> productSizes,
                              List<CategoryResponse> categories) implements Serializable {
    public boolean isOutOfStock() {
        if (productSizes == null || productSizes.isEmpty()) {
            return safeStock(inStock) <= 0;
        }

        return productSizes.stream().allMatch(size -> size.inStock() == null || size.inStock() <= 0);
    }

    public UUID firstAvailableSizeId() {
        if (productSizes == null) {
            return null;
        }

        return productSizes.stream()
                .filter(size -> size.inStock() != null && size.inStock() > 0)
                .map(ProductSizeResponse::id)
                .findFirst()
                .orElse(null);
    }

    public int firstAvailableStock() {
        if (productSizes == null || productSizes.isEmpty()) {
            return safeStock(inStock);
        }

        return productSizes.stream()
                .filter(size -> size.inStock() != null && size.inStock() > 0)
                .map(ProductSizeResponse::inStock)
                .findFirst()
                .orElse(0);
    }

    private static int safeStock(Integer stock) {
        return stock == null ? 0 : stock;
    }
}
