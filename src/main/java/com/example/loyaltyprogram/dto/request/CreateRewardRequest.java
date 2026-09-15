package com.example.loyaltyprogram.dto.request;

import java.time.LocalDateTime;

public record CreateRewardRequest(
        String name,
        int pointsCost,
        Integer availableQuantity,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
