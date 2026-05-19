package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Cart Response
 */
public record CartResponse(
        List<CartItemResponse> items,
        int totalQuantity,
        BigDecimal subtotal
) implements Serializable {
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
