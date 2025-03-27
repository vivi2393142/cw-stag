package edu.uob.exception;

import java.io.Serial;

public class UserException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String message;

    public UserException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
