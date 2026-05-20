package io.github.ptus04.server.dto.chat;

import java.io.Serializable;
import java.math.BigDecimal;

public record ChatOrderDetailDto(
        String size,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) implements Serializable {
}
