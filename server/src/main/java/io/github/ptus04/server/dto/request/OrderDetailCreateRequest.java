package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.OrderDetail}
 */
public record OrderDetailCreateRequest(UUID productId, UUID productSizeId,
                                       @NotNull @Positive(message = "Số lượng sản phẩm phải là số dương") Integer quantity,
                                       @NotNull BigDecimal price) implements Serializable {
}