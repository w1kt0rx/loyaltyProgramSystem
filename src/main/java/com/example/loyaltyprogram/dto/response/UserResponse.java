package com.example.loyaltyprogram.dto.response;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        List<ProgramSummaryResponse> programs
) {
}
