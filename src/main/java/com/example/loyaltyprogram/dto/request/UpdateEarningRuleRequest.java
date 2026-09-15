package com.example.loyaltyprogram.dto.request;

import java.time.LocalDateTime;

public record UpdateEarningRuleRequest(
        Long programId,
        int points,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
