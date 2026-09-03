package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.request.CreateRewardRequest;
import com.example.loyaltyprogram.dto.request.UpdateRewardRequest;
import com.example.loyaltyprogram.dto.response.RewardResponse;
import com.example.loyaltyprogram.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;

    @PostMapping("/programs/{programId}/rewards")
    public ResponseEntity<RewardResponse> createReward(@PathVariable Long programId, @RequestBody CreateRewardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rewardService.createReward(programId, request));
    }

    @GetMapping("/programs/{programId}/rewards")
    public ResponseEntity<List<RewardResponse>> listRewards(@PathVariable Long programId) {
        return ResponseEntity.ok(rewardService.listRewards(programId));
    }

    @GetMapping("/rewards/{rewardId}")
    public ResponseEntity<RewardResponse> getReward(@PathVariable Long rewardId) {
        return ResponseEntity.ok(rewardService.getReward(rewardId));
    }

    @PostMapping("/rewards/{rewardId}")
    public ResponseEntity<RewardResponse> updateReward(@PathVariable Long rewardId, @RequestBody UpdateRewardRequest request) {
        return ResponseEntity.ok(rewardService.updateReward(rewardId, request));
    }

    @DeleteMapping("/rewards/{rewardId}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long rewardId) {
        rewardService.deleteReward(rewardId);
        return ResponseEntity.noContent().build();
    }
}
