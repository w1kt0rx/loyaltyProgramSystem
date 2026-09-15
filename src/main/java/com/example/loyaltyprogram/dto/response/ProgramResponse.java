package com.example.loyaltyprogram.dto.response;

import java.time.LocalDateTime;

public record ProgramResponse(
        Long id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
