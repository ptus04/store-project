package io.github.ptus04.server.exception;

import lombok.experimental.StandardException;

@StandardException
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
