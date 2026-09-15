package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class EarningRuleNotFoundException extends BusinessException {
    public EarningRuleNotFoundException(Long earningRuleId) {
        super("EARNING_RULE_NOT_FOUND", "Earning rule not found: id=" + earningRuleId, HttpStatus.NOT_FOUND);
    }
}
