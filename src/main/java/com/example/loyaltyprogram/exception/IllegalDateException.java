package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class IllegalDateException extends BusinessException {
    public IllegalDateException(String message) {
        super("INVALID_DATE", message, HttpStatus.CONFLICT);
    }
}
