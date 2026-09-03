package com.example.loyaltyprogram.exception;

import org.springframework.http.HttpStatus;

public class RewardUnavailableException extends BusinessException {
    public RewardUnavailableException(Long rewardId) {
        super("REWARD_UNAVAILABLE", "Reward is not available: id=" + rewardId, HttpStatus.CONFLICT);
    }
}