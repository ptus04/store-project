package io.github.ptus04.server.exception;

public class ExistedPhoneNumberException extends RuntimeException {
    public ExistedPhoneNumberException(String message) {
        super(message);
    }
}
