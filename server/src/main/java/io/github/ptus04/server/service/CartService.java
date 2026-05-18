package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.internal.Cart;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public interface CartService {
    Cart getCart(HttpSession session);

    Cart addItem(HttpSession session, UUID productSizeId, int quantity);

    Cart updateItemQuantity(HttpSession session, UUID productSizeId, int quantity);

    Cart removeItem(HttpSession session, UUID productSizeId);
}
