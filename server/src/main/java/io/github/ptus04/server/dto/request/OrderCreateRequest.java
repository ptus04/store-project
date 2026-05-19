package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.enums.OrderPaymentMethodEnum;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link Order}
 */
public record OrderCreateRequest(@NotNull OrderPaymentMethodEnum paymentMethod, String note,
                                 List<OrderDetailCreateRequest> orderDetails,
                                 OrderShippingAddressCreateRequest orderShippingAddress) implements Serializable {
    public OrderCreateRequest(@NotNull OrderPaymentMethodEnum paymentMethod,
                              OrderShippingAddressCreateRequest orderShippingAddress) {
        this(paymentMethod, null, null, orderShippingAddress);
    }
}