package com.genpt.api.controller;

import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.exception.ResourceNotFoundException;
import com.genpt.api.exception.XmlParsingException;
import com.genpt.api.util.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(apiError, httpStatus);
    }
    
    @ExceptionHandler(XmlParsingException.class)
    public ResponseEntity<ApiError> handleXmlParsing(ResourceNotFoundException ex, HttpServletRequest request) {
        
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(apiError, httpStatus);
    }
    
    @ExceptionHandler(EmptyResourceException.class)
    public ResponseEntity<ApiError> handleEmptyResource(EmptyResourceException ex, HttpServletRequest request) {
        
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(apiError, httpStatus);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
        
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(apiError, httpStatus);
    }
}