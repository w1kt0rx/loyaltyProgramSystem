package com.example.loyaltyprogram.dto.response;

public record EarnPointsResponse(
        Long transactionId,
        String eventType,
        int pointsEarned,
        int newBalance,
        String referenceId
) {
}