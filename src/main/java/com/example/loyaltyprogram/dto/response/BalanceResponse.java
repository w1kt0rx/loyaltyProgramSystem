package com.example.loyaltyprogram.dto.response;

public record BalanceResponse(
        Long membershipId,
        Long userId,
        Long programId,
        String programName,
        int pointsBalance
) {
}
