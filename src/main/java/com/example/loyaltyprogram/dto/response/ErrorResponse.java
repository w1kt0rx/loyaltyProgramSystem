package com.example.loyaltyprogram.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String errorCode,
        String message,
        List<FieldErrorDetail> fieldErrors
) {
    public ErrorResponse(int status, String errorCode, String message) {
        this(LocalDateTime.now(), status, errorCode, message, List.of());
    }
}
