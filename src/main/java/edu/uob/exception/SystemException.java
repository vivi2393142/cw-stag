package edu.uob.exception;

import java.io.Serial;

public class SystemException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public SystemException(String message) {
        super(message);
    }
}
