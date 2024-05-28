package com.genpt.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response template for returning errors in JSON format.
 *
 * @see com.genpt.api.controller.DefaultExceptionHandler
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT) // excludes empty fields
public class ApiError {
    
    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();
    
    private int status;
    private String error;
    private String message;
    private String path;
}
