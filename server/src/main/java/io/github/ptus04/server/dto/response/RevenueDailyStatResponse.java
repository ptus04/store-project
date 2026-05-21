package io.github.ptus04.server.dto.response;

import java.math.BigDecimal;

public record RevenueDailyStatResponse(
        String date,
        BigDecimal revenue
) {
}