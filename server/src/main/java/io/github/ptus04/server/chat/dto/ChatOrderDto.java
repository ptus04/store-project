package io.github.ptus04.server.chat.dto;

import io.github.ptus04.server.dto.response.OrderResponse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record ChatOrderDto(
        String orderCode,
        Instant orderDate,
        String status,
        String paymentMethod,
        BigDecimal total,
        List<ChatOrderDetailDto> details
) implements Serializable {
    public static ChatOrderDto fromOrderResponse(OrderResponse response) {
        if (response == null) return null;

        List<ChatOrderDetailDto> details = response.orderDetails() != null ?
                response.orderDetails().stream().map(d -> new ChatOrderDetailDto(
                        d.productSize(),
                        d.quantity(),
                        d.price(),
                        d.subtotal()
                )).collect(Collectors.toList()) : List.of();

        return new ChatOrderDto(
                response.orderCode(),
                response.orderDate(),
                response.status() != null ? response.status().name() : "UNKNOWN",
                response.paymentMethod() != null ? response.paymentMethod().name() : "UNKNOWN",
                response.total(),
                details
        );
    }
}
