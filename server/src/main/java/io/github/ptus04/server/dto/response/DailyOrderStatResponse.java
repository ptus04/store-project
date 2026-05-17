package io.github.ptus04.server.dto.response;

public record DailyOrderStatResponse(
        String date,
        Long orders
) {
}