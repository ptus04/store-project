package io.github.ptus04.server.dto.internal;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class Cart implements Serializable {
    private List<CartItem> items = new ArrayList<>();

    public Optional<CartItem> findItem(UUID productSizeId) {
        return items.stream()
                .filter(item -> item.getProductSizeId().equals(productSizeId))
                .findFirst();
    }

    public void removeItem(UUID productSizeId) {
        items.removeIf(item -> item.getProductSizeId().equals(productSizeId));
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
