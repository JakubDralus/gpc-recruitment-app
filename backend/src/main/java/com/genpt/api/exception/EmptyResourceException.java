package com.genpt.api.exception;

public class EmptyResourceException extends RuntimeException {
    
    public EmptyResourceException(String message) {
        super(message);
    }
}
