package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class RewardNotFoundException extends BusinessException {
    public RewardNotFoundException(Long rewardId) {
        super("REWARD_NOT_FOUND", "Reward not found: id=" + rewardId, HttpStatus.NOT_FOUND);
    }
}
