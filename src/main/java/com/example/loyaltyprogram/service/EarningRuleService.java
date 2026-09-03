package com.example.loyaltyprogram.service;

import com.example.loyaltyprogram.dto.request.CreateEarningRuleRequest;
import com.example.loyaltyprogram.dto.request.UpdateEarningRuleRequest;
import com.example.loyaltyprogram.dto.response.EarningRuleResponse;
import com.example.loyaltyprogram.exception.ConflictException;
import com.example.loyaltyprogram.exception.EarningRuleNotFoundException;
import com.example.loyaltyprogram.exception.ProgramExpiredException;
import com.example.loyaltyprogram.exception.ProgramNotFoundException;
import com.example.loyaltyprogram.mapper.EarningRuleMapper;
import com.example.loyaltyprogram.model.EarningEventType;
import com.example.loyaltyprogram.model.EarningRule;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Period;
import com.example.loyaltyprogram.repository.EarningRuleRepository;
import com.example.loyaltyprogram.repository.LoyaltyProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EarningRuleService {

    private final EarningRuleRepository earningRuleRepository;
    private final LoyaltyProgramRepository programRepository;
    private final EarningRuleMapper earningRuleMapper;

    @Transactional
    public EarningRuleResponse createRule(Long programId, CreateEarningRuleRequest request) {
        LoyaltyProgram program = findProgramById(programId);
        if (!program.isActiveAt(LocalDateTime.now())) {
            throw new ProgramExpiredException(program.getId());
        }
        Period newPeriod = new Period(request.startDate(), request.endDate());
        checkNoOverlap(programId, request.eventType(), newPeriod, null);
        EarningRule earningRule = earningRuleMapper.toEntity(request);
        earningRule.setPeriod(newPeriod);
        program.addEarningRule(earningRule);
        EarningRule saved = earningRuleRepository.save(earningRule);
        log.info("Created earning rule id={} program={} event={}", saved.getId(), programId, request.eventType());
        return earningRuleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EarningRuleResponse> listRules(Long programId) {
        findProgramById(programId);
        return earningRuleRepository.findByProgramIdAndEventType(programId, null) == null
                ? List.of()
                : earningRuleRepository.findAll().stream()
                .filter(r -> r.getProgram().getId().equals(programId))
                .map(earningRuleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EarningRuleResponse getEarningRule(Long earningRuleId) {
        return earningRuleMapper.toResponse(findEarningRuleById(earningRuleId));
    }

    @Transactional
    public EarningRuleResponse updateEarningRule(Long earningRuleId, UpdateEarningRuleRequest request) {
        EarningRule earningRule = findEarningRuleById(earningRuleId);
        Period newPeriod = new Period(request.startDate(), request.endDate());
        checkNoOverlap(earningRule.getProgram().getId(), earningRule.getEventType(), newPeriod, earningRule.getId());
        return earningRuleMapper.toResponse(earningRule.update(request));
    }

    @Transactional
    public void delete(Long earningRuleID) {
        EarningRule earningRule = findEarningRuleById(earningRuleID);
        earningRuleRepository.delete(earningRule);
    }

    private LoyaltyProgram findProgramById(Long programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException(programId));
    }

    private EarningRule findEarningRuleById(Long earningRuleId) {
        return earningRuleRepository.findById(earningRuleId)
                .orElseThrow(() -> new EarningRuleNotFoundException(earningRuleId));
    }

    private void checkNoOverlap(Long programId, EarningEventType eventType,
                                Period newPeriod, Long excludeRuleId) {
        List<EarningRule> sameEventRules = earningRuleRepository.findByProgramIdAndEventType(programId, eventType);
        boolean overlaps = sameEventRules.stream()
                .filter(existing -> !existing.getId().equals(excludeRuleId))
                .anyMatch(existing -> existing.getPeriod().overlaps(newPeriod));
        if (overlaps) {
            throw new ConflictException("EARNING_RULE_OVERLAP",
                    "An active earning rule for event " + eventType + " already exists in an overlapping period");
        }
    }
}
