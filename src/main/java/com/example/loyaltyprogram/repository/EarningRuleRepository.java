package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.EarningEventType;
import com.example.loyaltyprogram.model.EarningRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EarningRuleRepository extends JpaRepository<EarningRule, Long> {
    List<EarningRule> findByProgramIdAndEventType(Long programId, EarningEventType eventType);
}
