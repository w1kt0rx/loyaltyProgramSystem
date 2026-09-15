package com.example.loyaltyprogram.dto.response;

public record RewardResponse(
        Long id,
        Long programId,
        String name,
        int pointsCost,
        Integer availableQuantity
) {
}
