package io.github.ptus04.server.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaidEvent(
        String orderId,
        String orderCode,
        String userId,
        String email,
        String buyerName,
        String buyerPhone,
        String buyerAddress,
        List<OrderItem> items
) {
    public record OrderItem(String itemCode, String itemName, int quantity, BigDecimal unitPrice, float discountTax) {
    }
}
