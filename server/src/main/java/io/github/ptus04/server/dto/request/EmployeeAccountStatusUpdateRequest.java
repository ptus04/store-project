package io.github.ptus04.server.dto.request;

import java.time.Instant;

public record EmployeeAccountStatusUpdateRequest(
        Instant disabledAt
) {
}

