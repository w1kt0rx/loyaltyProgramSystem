package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "User not found: id=" + id, HttpStatus.NOT_FOUND);
    }
}