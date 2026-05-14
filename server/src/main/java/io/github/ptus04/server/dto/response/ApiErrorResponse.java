package io.github.ptus04.server.dto.response;

import java.io.Serializable;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        long timestamp,
        String errorCode
) implements Serializable {
}
