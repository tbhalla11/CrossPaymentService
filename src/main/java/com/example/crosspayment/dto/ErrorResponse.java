package com.example.crosspayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    /**
     *
     * Timestamp of when the error occurred
     *
     */
    private LocalDateTime timestamp;

    /**
     *
     * HTTP status code
     *
     */
    private int status;

    /**
     *
     * Error message
     *
     */
    private String message;

    /**
     *
     *  Validation errors, if any
     *
     */
    private Map<String, String> validationErrors;
}
