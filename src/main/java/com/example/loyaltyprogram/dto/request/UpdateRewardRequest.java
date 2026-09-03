package com.example.loyaltyprogram.dto.request;

public record UpdateRewardRequest(
        String name,
        int pointsCost,
        Integer availableQuantity
) {
}
