package com.example.loyaltyprogram.dto.request;

public record CreateUserRequest(
        String email,
        String firstName,
        String lastName,
        Long programId
) {
}
