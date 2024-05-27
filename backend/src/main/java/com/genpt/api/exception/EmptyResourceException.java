package com.genpt.api.exception;

public class EmptyResourceException extends RuntimeException {
    
    public EmptyResourceException(String message) {
        super(message);
    }
    
    public EmptyResourceException(Throwable cause) {
        super(cause);
    }
    
    public EmptyResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}