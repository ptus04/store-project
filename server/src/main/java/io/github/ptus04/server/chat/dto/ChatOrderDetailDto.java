package io.github.ptus04.server.chat.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ChatOrderDetailDto(
        String size,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) implements Serializable {
}
