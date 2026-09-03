package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class ProgramNotFoundException extends BusinessException {
    public ProgramNotFoundException(Long id) {
        super("PROGRAM_NOT_FOUND", "Program not found: id=" + id, HttpStatus.NOT_FOUND);
    }
}