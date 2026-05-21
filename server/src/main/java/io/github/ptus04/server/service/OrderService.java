package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.OrderCreateRequest;
import io.github.ptus04.server.dto.response.OrderResponse;
import io.github.ptus04.server.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(@NotNull UUID userId, OrderCreateRequest orderCreateRequest);

    OrderResponse getOrderById(UUID id);

    OrderResponse getOrderByIdForUser(UUID id, UUID userId);

    OrderResponse getOrderByOrderCode(String orderCode);

    Page<OrderResponse> searchOrders(OrderStatusEnum status, String search, int page, int size);

    Page<OrderResponse> searchOrdersByUserId(UUID userId, String search, int page, int size);

    OrderResponse updateOrderStatus(UUID id, OrderStatusEnum status);

    OrderResponse cancelOrder(UUID id, UUID userId, String cancellationReason);

    String generateOrderCode();
}