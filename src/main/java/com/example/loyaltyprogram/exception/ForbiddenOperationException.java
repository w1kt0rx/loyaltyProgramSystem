package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends BusinessException {
    public ForbiddenOperationException(String message) {
        super("FORBIDDEN_OPERATION", message, HttpStatus.FORBIDDEN);
    }
}