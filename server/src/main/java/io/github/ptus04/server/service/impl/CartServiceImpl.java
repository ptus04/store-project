package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.internal.Cart;
import io.github.ptus04.server.dto.internal.CartItem;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.entity.ProductSize;
import io.github.ptus04.server.exception.CartStockException;
import io.github.ptus04.server.repository.ProductSizeRepository;
import io.github.ptus04.server.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private static final String CART_SESSION_KEY = "cart";

    private final ProductSizeRepository productSizeRepository;

    @Override
    public Cart getCart(HttpSession session) {
        Object value = session.getAttribute(CART_SESSION_KEY);
        if (value instanceof Cart cart) {
            return cart;
        }

        Cart cart = new Cart();
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart addItem(HttpSession session, UUID productSizeId, int quantity) {
        validatePositiveQuantity(quantity);

        ProductSize productSize = getProductSize(productSizeId);
        Cart cart = getCart(session);
        int currentQuantity = cart.findItem(productSizeId)
                .map(CartItem::getQuantity)
                .orElse(0);
        int nextQuantity = currentQuantity + quantity;

        validateStock(productSize, nextQuantity);
        CartItem item = cart.findItem(productSizeId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProductSizeId(productSizeId);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        fillItem(item, productSize);
        item.setQuantity(nextQuantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart updateItemQuantity(HttpSession session, UUID productSizeId, int quantity) {
        validatePositiveQuantity(quantity);

        ProductSize productSize = getProductSize(productSizeId);
        validateStock(productSize, quantity);

        Cart cart = getCart(session);
        CartItem item = cart.findItem(productSizeId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        fillItem(item, productSize);
        item.setQuantity(quantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    public Cart removeItem(HttpSession session, UUID productSizeId) {
        Cart cart = getCart(session);
        cart.removeItem(productSizeId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    private ProductSize getProductSize(UUID productSizeId) {
        return productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new EntityNotFoundException("Product size not found"));
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity < 1) {
            throw CartStockException.invalidQuantity();
        }
    }

    private void validateStock(ProductSize productSize, int quantity) {
        int inStock = productSize.getInStock();
        if (inStock <= 0) {
            throw CartStockException.outOfStock(productSize.getName());
        }
        if (quantity > inStock) {
            throw CartStockException.insufficientStock(productSize.getName(), inStock);
        }
    }

    private void fillItem(CartItem item, ProductSize productSize) {
        Product product = productSize.getProduct();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setSizeName(productSize.getName());
        item.setInStock(productSize.getInStock());
        item.setUnitPrice(calculateUnitPrice(product));
        item.setImageFile(product.getProductImages()
                .stream()
                .findFirst()
                .map(image -> image.getFile())
                .orElse(null));
    }

    private BigDecimal calculateUnitPrice(Product product) {
        BigDecimal discount = BigDecimal.valueOf(product.getDiscount() == null ? 0 : product.getDiscount().doubleValue());
        return product.getPrice()
                .multiply(BigDecimal.ONE.subtract(discount))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
