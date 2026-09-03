package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class ProgramExpiredException extends BusinessException {
    public ProgramExpiredException(Long programId) {
        super("PROGRAM_EXPIRED", "Program is not active: id=" + programId, HttpStatus.CONFLICT);
    }
}