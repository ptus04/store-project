package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.OrderCreateRequest;
import io.github.ptus04.server.dto.response.OrderResponse;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(@NotNull UUID userId, OrderCreateRequest orderCreateRequest);

    OrderResponse getOrderById(UUID id);

    OrderResponse getOrderByOrderCode(String orderCode);

    String generateOrderCode();
}
