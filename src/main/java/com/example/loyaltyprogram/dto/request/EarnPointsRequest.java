package com.example.loyaltyprogram.dto.request;

import com.example.loyaltyprogram.model.EarningEventType;

public record EarnPointsRequest(
        EarningEventType eventType,
        Long programId,
        Long earningRuleId,
        String referenceId
) {
}