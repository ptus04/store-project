package io.github.ptus04.server.dto.response;

import io.github.ptus04.server.enums.OrderPaymentMethodEnum;
import io.github.ptus04.server.enums.OrderStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.Order}
 */
public record OrderResponse(UUID id, String orderCode, UserResponse user, Instant orderDate,
                            Instant shippingDate, OrderPaymentMethodEnum paymentMethod, OrderStatusEnum status,
                            BigDecimal total, String note, String cancellationReason, Instant createdAt,
                            Instant updatedAt, Set<OrderDetailResponse> orderDetails,
                            OrderShippingAddressResponse orderShippingAddress,
                            Set<TransactionResponse> transactions) implements Serializable {
}