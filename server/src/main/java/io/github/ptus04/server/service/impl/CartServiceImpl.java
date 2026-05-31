package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.internal.Cart;
import io.github.ptus04.server.dto.internal.CartItem;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.entity.ProductSize;
import io.github.ptus04.server.exception.CartStockException;
import io.github.ptus04.server.repository.ProductSizeRepository;
import io.github.ptus04.server.repository.ProductRepository;
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

    private final ProductRepository productRepository;
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
    public Cart addItem(HttpSession session, UUID productId, UUID productSizeId, int quantity) {
        validatePositiveQuantity(quantity);

        Product product = getProduct(productId);
        ProductSize productSize = getProductSize(product, productSizeId);
        Cart cart = getCart(session);
        int currentQuantity = cart.findItem(product.getId(), productSizeId)
                .map(CartItem::getQuantity)
                .orElse(0);
        int nextQuantity = currentQuantity + quantity;

        validateStock(product, productSize, nextQuantity);
        CartItem item = cart.findItem(product.getId(), productSizeId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProductSizeId(productSizeId);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        fillItem(item, product, productSize);
        item.setQuantity(nextQuantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart updateItemQuantity(HttpSession session, UUID itemId, int quantity) {
        validatePositiveQuantity(quantity);

        Cart cart = getCart(session);
        CartItem item = cart.findItemByItemId(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        Product product = getProduct(item.getProductId());
        ProductSize productSize = getProductSize(product, item.getProductSizeId());
        validateStock(product, productSize, quantity);
        fillItem(item, product, productSize);
        item.setQuantity(quantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    public Cart removeItem(HttpSession session, UUID itemId) {
        Cart cart = getCart(session);
        cart.removeItem(itemId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private ProductSize getProductSize(Product product, UUID productSizeId) {
        if (product.getProductSizes().isEmpty()) {
            if (productSizeId != null) {
                throw new EntityNotFoundException("Product size not found");
            }
            return null;
        }

        if (productSizeId == null) {
            throw new EntityNotFoundException("Product size is required");
        }

        ProductSize productSize = productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new EntityNotFoundException("Product size not found"));
        if (!productSize.getProduct().getId().equals(product.getId())) {
            throw new EntityNotFoundException("Product size not found");
        }
        return productSize;
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity < 1) {
            throw CartStockException.invalidQuantity();
        }
    }

    private void validateStock(Product product, ProductSize productSize, int quantity) {
        int inStock = productSize == null ? product.getInStock() : productSize.getInStock();
        if (inStock <= 0) {
            throw CartStockException.outOfStock(productSize == null ? product.getName() : productSize.getName());
        }
        if (quantity > inStock) {
            throw CartStockException.insufficientStock(productSize == null ? product.getName() : productSize.getName(), inStock);
        }
    }

    private void fillItem(CartItem item, Product product, ProductSize productSize) {
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductSizeId(productSize == null ? null : productSize.getId());
        item.setSizeName(productSize == null ? null : productSize.getName());
        item.setInStock(productSize == null ? product.getInStock() : productSize.getInStock());
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
