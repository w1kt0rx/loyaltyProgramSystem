package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class MembershipNotFoundException extends BusinessException {
    public MembershipNotFoundException(Long userId, Long programId) {
        super("MEMBERSHIP_NOT_FOUND", "User id=" + userId + " is not a member of program id=" + programId, HttpStatus.NOT_FOUND);
    }
}
