package io.github.ptus04.server.dto.internal;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CartItem implements Serializable {
    private UUID productId;
    private UUID productSizeId;
    private String productName;
    private String sizeName;
    private String imageFile;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private int quantity;
    private int inStock;

    public UUID getItemId() {
        return productSizeId != null ? productSizeId : productId;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
