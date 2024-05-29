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

/**
 * Global exception handler for handling various types of exceptions thrown
 * within the application and providing consistent error responses.
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    
    /**
     * Handle ResourceNotFoundException and return a ResponseEntity with an appropriate error message and status code.
     *
     * @param ex      The ResourceNotFoundException object.
     * @param request The HttpServletRequest object.
     * @return A ResponseEntity containing the error response.
     * @see ApiError
     */
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
    
    /**
     * Handle EmptyResourceException and return a ResponseEntity with an appropriate error message and status code.
     *
     * @param ex      The EmptyResourceException object.
     * @param request The HttpServletRequest object.
     * @return A ResponseEntity containing the error response.
     * @see ApiError
     */
    @ExceptionHandler(EmptyResourceException.class)
    public ResponseEntity<ApiError> handleEmptyResource(EmptyResourceException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(apiError, httpStatus);
    }
    
    /**
     * Handle XmlParsingException and return a ResponseEntity with an appropriate error message and status code.
     *
     * @param ex      The XmlParsingException object.
     * @param request The HttpServletRequest object.
     * @return A ResponseEntity containing the error response.
     * @see ApiError
     */
    @ExceptionHandler(XmlParsingException.class)
    public ResponseEntity<ApiError> handleXmlParsing(XmlParsingException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError apiError = ApiError.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(apiError, httpStatus);
    }
    
    /**
     * Handle generic Exception and return a ResponseEntity with an appropriate error message and status code.
     *
     * @param ex      The Exception object.
     * @param request The HttpServletRequest object.
     * @return A ResponseEntity containing the error response.
     * @see ApiError
     */
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
