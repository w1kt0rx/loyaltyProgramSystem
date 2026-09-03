package com.example.loyaltyprogram.dto.request;

import com.example.loyaltyprogram.model.EarningEventType;

import java.time.LocalDateTime;

public record CreateEarningRuleRequest
        (        EarningEventType eventType,
                 int points,
                 LocalDateTime startDate,
                 LocalDateTime endDate
        ) {
}
