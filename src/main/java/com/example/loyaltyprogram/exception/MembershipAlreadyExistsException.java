package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class MembershipAlreadyExistsException extends BusinessException {
    public MembershipAlreadyExistsException(Long userId, Long programId) {
        super("MEMBERSHIP_ALREADY_EXISTS",
                "User id=" + userId + " already belongs to program id=" + programId, HttpStatus.CONFLICT);
    }
}