package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class NoEarningRuleException extends BusinessException {
    public NoEarningRuleException(Long programId, String eventType) {
        super("NO_EARNING_RULE",
                "No active earning rule for program id=" + programId + " and event=" + eventType, HttpStatus.CONFLICT);
    }
}