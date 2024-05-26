package com.genpt.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Generic response template for clean JSON response structure.
 * @param <T> parameter of the data to be returned
 */
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT) // excludes empty fields
public class ApiResponse<T> {
    
    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();
    
    @Builder.Default
    private int status = HttpStatus.OK.value();
    
    private String message;
    
    private T data;
}
