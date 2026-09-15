package com.example.loyaltyprogram.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FieldValidationException extends BusinessException {
    private final String field;
    private final Object rejectedValue;

    public FieldValidationException(String field, String message, Object rejectedValue) {
        super("VALIDATION_ERROR", field + " " + message, HttpStatus.BAD_REQUEST);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }
}