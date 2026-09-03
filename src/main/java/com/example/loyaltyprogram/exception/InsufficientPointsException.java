package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class InsufficientPointsException extends BusinessException {
    public InsufficientPointsException(Long membershipId, int required, int available) {
        super("INSUFFICIENT_POINTS",
                "Insufficient points for membership id=" + membershipId
                        + " (required=" + required + ", available=" + available + ")", HttpStatus.CONFLICT);
    }
}
