package io.github.ptus04.server.dto.response;

public record OrderDailyStatResponse(
        String date,
        Long orders
) {
}