package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByProgramId(Long programId);
}
