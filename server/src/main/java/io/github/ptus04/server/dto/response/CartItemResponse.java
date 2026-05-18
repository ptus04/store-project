package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for Cart Item
 */
public record CartItemResponse(
        UUID productId,
        UUID productSizeId,
        String productName,
        String sizeName,
        String imageFile,
        BigDecimal unitPrice,
        int quantity,
        int inStock,
        BigDecimal subtotal
) implements Serializable {
}
