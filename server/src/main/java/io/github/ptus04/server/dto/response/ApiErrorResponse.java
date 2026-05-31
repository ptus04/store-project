package io.github.ptus04.server.dto.response;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

public record ApiErrorResponse<T>(
        int status, String error, String message, String path, long timestamp, String errorCode,
        Map<Object, String> fieldErrors, T data
) implements Serializable {
    public ApiErrorResponse(HttpStatus status, String message, String path, String errorCode) {
        this(status.value(), status.getReasonPhrase(), message, path, System.currentTimeMillis(), errorCode, null, null);
    }

    public ApiErrorResponse(HttpStatus status, String message, String path, String errorCode, Map<Object, String> fieldErrors) {
        this(status.value(), status.getReasonPhrase(), message, path, System.currentTimeMillis(), errorCode, fieldErrors, null);
    }

    public ApiErrorResponse(HttpStatus status, String message, String path, String errorCode, T data) {
        this(status.value(), status.getReasonPhrase(), message, path, System.currentTimeMillis(), errorCode, null, data);
    }
}
