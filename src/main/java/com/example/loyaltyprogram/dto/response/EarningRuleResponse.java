package com.example.loyaltyprogram.dto.response;

import com.example.loyaltyprogram.model.EarningEventType;

import java.time.LocalDateTime;

public record EarningRuleResponse(
        Long id,
        Long programId,
        EarningEventType eventType,
        int points,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
