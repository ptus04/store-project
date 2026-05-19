package io.github.ptus04.server.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.OrderShippingAddress}
 */
public record OrderShippingAddressResponse(UUID orderId, @NotNull @Size(max = 128) String name,
                                           @NotNull @Size(max = 10) String phone, @NotNull @Size(max = 32) String city,
                                           @NotNull @Size(max = 32) String district,
                                           @NotNull @Size(max = 128) String ward,
                                           @NotNull @Size(max = 128) String address) implements Serializable {
}