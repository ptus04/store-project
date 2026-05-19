package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.entity.OrderShippingAddress;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link OrderShippingAddress}
 */
public record OrderShippingAddressCreateRequest(@NotNull @Size(max = 32) String city,
                                                @NotNull @Size(max = 32) String district,
                                                @NotNull @Size(max = 128) String ward,
                                                @NotNull @Size(max = 128) String address) implements Serializable {
}