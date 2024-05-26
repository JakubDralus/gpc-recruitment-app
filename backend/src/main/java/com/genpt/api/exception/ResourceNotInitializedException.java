package com.genpt.api.exception;

public class ResourceNotInitializedException extends RuntimeException {
    
    public ResourceNotInitializedException(String message) {
        super(message);
    }
    
    public ResourceNotInitializedException(Throwable cause) {
        super(cause);
    }
    
    public ResourceNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}