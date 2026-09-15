package com.example.loyaltyprogram.dto.response;

import java.time.LocalDateTime;

public record PointsHistoryResponse(
        Long id,
        String type,
        int points,
        String description,
        String programName,
        LocalDateTime occurredAt
) {
}
