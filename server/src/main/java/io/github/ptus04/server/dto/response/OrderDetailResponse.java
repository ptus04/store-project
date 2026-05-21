package io.github.ptus04.server.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.OrderDetail}
 */
public record OrderDetailResponse(UUID id, ProductResponse product, @Size(max = 4) String productSize,
                                  @NotNull Integer quantity, @NotNull BigDecimal price,
                                  @NotNull BigDecimal subtotal) implements Serializable {
}
