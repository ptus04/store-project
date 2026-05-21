package io.github.ptus04.server.dto.internal;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class Cart implements Serializable {
    private List<CartItem> items = new ArrayList<>();

    public Optional<CartItem> findItem(UUID productId, UUID productSizeId) {
        return items.stream()
                .filter(item -> Objects.equals(item.getProductId(), productId)
                        && Objects.equals(item.getProductSizeId(), productSizeId))
                .findFirst();
    }

    public Optional<CartItem> findItemByItemId(UUID itemId) {
        return items.stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst();
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getItemId().equals(itemId));
    }

    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public BigDecimal getSubtotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
