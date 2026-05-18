package io.github.ptus04.server.controller.advice;

import io.github.ptus04.server.dto.response.ApiErrorResponse;
import io.github.ptus04.server.exception.BusinessConstraintViolationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalApiExceptionControllerAdvice {
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ApiErrorResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex,
                                                                             WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        String path = getRequestPath(request);
        String errorCode = "ENTITY_NOT_FOUND";

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse<>(status, message, path, errorCode));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Validation failed for one or more fields (lv2).";
        String path = getRequestPath(request);
        String errorCode = "VALIDATION_ERROR";
        Map<Object, String> fieldErrors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                                ConstraintViolation::getPropertyPath,
                                ConstraintViolation::getMessage,
                                (existing, replacement) -> existing + "; " + replacement
                        )
                );

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse<>(status, message, path, errorCode, fieldErrors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Validation failed for one or more fields (lv1).";
        String path = getRequestPath(request);
        String errorCode = "VALIDATION_ERROR";
        Map<Object, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                                FieldError::getField,
                                FieldError::getDefaultMessage,
                                (existing, replacement) -> existing + "; " + replacement
                        )
                );

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse<>(status, message, path, errorCode, fieldErrors));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiErrorResponse<?>> handleEntityExistsException(
            EntityExistsException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage();
        String path = getRequestPath(request);
        String errorCode = "ENTITY_ALREADY_EXISTS";
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse<>(status, message, path, errorCode));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse<?>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "The request could not be completed due to a database constraint error.";
        String path = getRequestPath(request);
        String errorCode = "DATA_INTEGRITY_VIOLATION";

        log.atError().setMessage("Data integrity violation: {}").addArgument(ex.getMessage()).log();

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse<>(status, message, path, errorCode));
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse<?>> handleBadCredentialsException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = ex.getMessage();
        String path = getRequestPath(request);
        String errorCode = "UNAUTHORIZED";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse<>(status, message, path, errorCode));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiErrorResponse<?>> handleException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred. Please try again later.";
        String path = getRequestPath(request);
        String errorCode = "INTERNAL_SERVER_ERROR";
        log.atError().setMessage("An unexpected error occurred: {}").addArgument(ex::getMessage).log();
        return ResponseEntity
                .internalServerError()
                .body(new ApiErrorResponse<>(status, message, path, errorCode));
    }

    @ExceptionHandler(value = BusinessConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse<?>> handleException(BusinessConstraintViolationException ex,
                                                               WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();
        String path = getRequestPath(request);
        String errorCode = "BUSINESS_CONSTRAINT_VIOLATION";
        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse<>(status, message, path, errorCode));
    }

    private String getRequestPath(WebRequest request) {
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }
        return description;
    }
}
