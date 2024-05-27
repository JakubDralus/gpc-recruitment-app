package com.genpt.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Error response template for clean JSON error.
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
