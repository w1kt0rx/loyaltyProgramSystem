package com.example.loyaltyprogram.dto.request;

import java.time.LocalDateTime;

public record CreateProgramRequest(
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
