package io.github.ptus04.server.exception;

import lombok.Getter;

@Getter
public class CartStockException extends RuntimeException {
    private final String code;

    private CartStockException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static CartStockException invalidQuantity() {
        return new CartStockException(
                "CART_INVALID_QUANTITY",
                "Số lượng phải lớn hơn 0"
        );
    }

    public static CartStockException outOfStock(String sizeName) {
        return new CartStockException(
                "CART_OUT_OF_STOCK",
                "Size " + sizeName + " của sản phẩm này đã hết hàng"
        );
    }

    public static CartStockException insufficientStock(String sizeName, int inStock) {
        return new CartStockException(
                "CART_INSUFFICIENT_STOCK",
                "Chỉ còn " + inStock + " sản phẩm size " + sizeName
        );
    }
}
