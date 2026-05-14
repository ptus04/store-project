package io.github.ptus04.server.exception;

public class PhoneExistedException extends RuntimeException {
    public PhoneExistedException(String message) {
        super(message);
    }
}
