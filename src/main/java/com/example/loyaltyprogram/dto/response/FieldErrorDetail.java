package com.example.loyaltyprogram.dto.response;

public record FieldErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {
}
