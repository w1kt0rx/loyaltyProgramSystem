package com.example.loyaltyprogram.service;

import com.example.loyaltyprogram.dto.request.CreateRewardRequest;
import com.example.loyaltyprogram.dto.request.UpdateRewardRequest;
import com.example.loyaltyprogram.dto.response.RewardResponse;
import com.example.loyaltyprogram.exception.ProgramExpiredException;
import com.example.loyaltyprogram.exception.ProgramNotFoundException;
import com.example.loyaltyprogram.exception.RewardNotFoundException;
import com.example.loyaltyprogram.mapper.RewardMapper;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Reward;
import com.example.loyaltyprogram.repository.LoyaltyProgramRepository;
import com.example.loyaltyprogram.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final LoyaltyProgramRepository programRepository;
    private final RewardMapper rewardMapper;

    @Transactional
    public RewardResponse createReward(Long programId, CreateRewardRequest request) {
        LoyaltyProgram program = findProgramById(programId);

        if(!program.isActiveAt(LocalDateTime.now())) {
            throw new ProgramExpiredException(programId);
        }
        Reward reward = rewardMapper.toEntity(request);
        program.addReward(reward);
        return rewardMapper.toResponse(rewardRepository.save(reward));
    }

    @Transactional(readOnly = true)
    public List<RewardResponse> listRewards(Long programId) {
        findProgramById(programId);
        return rewardRepository.findByProgramId(programId).stream()
                .map(rewardMapper::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public RewardResponse getReward(Long rewardId) {
        return rewardMapper.toResponse(findRewardById(rewardId));
    }

    @Transactional
    public RewardResponse updateReward(Long rewardId, UpdateRewardRequest request) {
        return rewardMapper.toResponse(findRewardById(rewardId).update(request));
    }

    @Transactional
    public void deleteReward(Long rewardId) {
        Reward reward = findRewardById(rewardId);
        rewardRepository.delete(reward);
    }

    private LoyaltyProgram findProgramById(Long programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException(programId));
    }

    private Reward findRewardById(Long rewardId) {
        return rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RewardNotFoundException(rewardId));
    }


}
