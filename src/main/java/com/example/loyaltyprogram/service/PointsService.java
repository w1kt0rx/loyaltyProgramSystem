package com.example.loyaltyprogram.service;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.EarnPointsRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.response.EarnPointsResponse;
import com.example.loyaltyprogram.dto.response.PointsHistoryResponse;
import com.example.loyaltyprogram.exception.*;
import com.example.loyaltyprogram.mapper.PageRequestMapper;
import com.example.loyaltyprogram.mapper.PointsTransactionMapper;
import com.example.loyaltyprogram.model.*;
import com.example.loyaltyprogram.repository.*;
import com.example.loyaltyprogram.validation.Validate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsService {
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final EarningRuleRepository earningRuleRepository;
    private final PointsTransactionRepository transactionRepository;
    private final CampaignRepository campaignRepository;
    private final PointsTransactionMapper pointsTransactionMapper;
    private final PageRequestMapper pageRequestMapper;

    @Transactional
    public EarnPointsResponse earnPoints(Long userId, EarnPointsRequest request) {
        log.debug("Attempting to earn points for userId={}, referenceId={}", userId, request.referenceId());
        Validate.notBlank(request.referenceId(), "referenceId");
        boolean variantA = request.eventType() != null && request.programId() != null;
        boolean variantB = request.earningRuleId() != null;

        if (variantA == variantB) {
            log.warn("Invalid earn request payload for userId={}. Provide either (eventType and programId) or earningRuleId", userId);
            throw new InvalidRequestException(
                    "Provide either (eventType and programId) or earningRuleId, not both or neither");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for userId={}", userId);
                    return new UserNotFoundException(userId);
                });

        if (user.isDeactivated()) {
            log.warn("Cannot earn points. User id={} is deactivated", user.getId());
            throw new ConflictException("USER_DEACTIVATED", "User id=" + user.getId() + " is deactivated");
        }

        Membership membership = resolveMembership(user, request);
        LocalDateTime now = LocalDateTime.now();
        LoyaltyProgram program = membership.getProgram();

        if (!program.isActiveAt(now)) {
            log.warn("Cannot earn points. Loyalty program id={} is inactive for userId={}", program.getId(), userId);
            throw new ProgramExpiredException(program.getId());
        }

        Optional<PointsTransaction> existing = transactionRepository
                .findByMembershipIdAndReferenceId(membership.getId(), request.referenceId());

        if (existing.isPresent()) {
            log.info("Duplicate earn request ignored (idempotent): membershipId={} referenceId={}",
                    membership.getId(), request.referenceId());
            return pointsTransactionMapper.toEarnResponse(existing.get());
        }

        EarningRule rule = resolveEarningRule(request, program, now);
        int basePoints = rule.getPoints();
        int finalPoints = applyCampaigns(basePoints, program, rule.getEventType(), now);
        PointsTransaction transaction = new PointsTransaction();
        transaction.setType(TransactionType.EARN);
        transaction.setPoints(finalPoints);
        transaction.setDescription("Earned via rule '" + rule.getEventType()
                + "' (ref=" + request.referenceId() + ")");
        transaction.setReferenceId(request.referenceId());
        transaction.setOccurredAt(now);
        PointsTransaction saved;
        try {
            membership.addTransaction(transaction);
            saved = transactionRepository.save(transaction);
            membership.setPointsBalance(membership.getPointsBalance() + finalPoints);
        } catch (DataIntegrityViolationException ex) {
            log.info("Concurrent duplicate earn request detected (referenceId={}), returning existing transaction",
                    request.referenceId());
            saved = transactionRepository
                    .findByMembershipIdAndReferenceId(membership.getId(), request.referenceId())
                    .orElseThrow(() -> ex);
        }
        log.info("User id={} earned {} points (base={}) in program id={} (rule event={}, referenceId={})",
                userId, finalPoints, basePoints, program.getId(), rule.getEventType(), request.referenceId());
        return pointsTransactionMapper.toEarnResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageDto<PointsHistoryResponse> getHistory(Long userId, Long programId, PageRequestDto pageRequest) {
        log.debug("Fetching points history for userId={}, programId={}", userId, programId);
        Pageable pageable = pageRequestMapper.toPageable(pageRequest);
        Membership membership = membershipRepository.findByUserIdAndProgramId(userId, programId)
                .orElseThrow(() -> {
                    log.warn("User id={} does not belong to program id={}", userId, programId);
                    return new ForbiddenOperationException(
                            "User id=" + userId + " does not belong to program id=" + programId);
                });
        return PageDto.from(transactionRepository
                .findByMembershipIdOrderByOccurredAtDesc(membership.getId(), pageable)
                .map(pointsTransactionMapper::toHistoryResponse));
    }

    private Membership resolveMembership(User user, EarnPointsRequest request) {
        if (request.earningRuleId() != null) {
            EarningRule rule = earningRuleRepository.findById(request.earningRuleId())
                    .orElseThrow(() -> {
                        log.warn("Earning rule not found: id={}", request.earningRuleId());
                        return new ConflictException("EARNING_RULE_NOT_FOUND",
                                "Earning rule not found: id=" + request.earningRuleId());
                    });

            return membershipRepository.findByUserIdAndProgramId(user.getId(), rule.getProgram().getId())
                    .orElseThrow(() -> {
                        log.warn("User id={} does not belong to program id={}", user.getId(), rule.getProgram().getId());
                        return new ForbiddenOperationException(
                                "User id=" + user.getId() + " does not belong to program id=" + rule.getProgram().getId());
                    });
        }

        if (request.programId() != null) {
            return membershipRepository.findByUserIdAndProgramId(user.getId(), request.programId())
                    .orElseThrow(() -> {
                        log.warn("User id={} does not belong to program id={}", user.getId(), request.programId());
                        return new ForbiddenOperationException(
                                "User id=" + user.getId() + " does not belong to program id=" + request.programId());
                    });
        }

        List<Membership> memberships = membershipRepository.findByUserId(user.getId());
        if (memberships.isEmpty()) {
            log.warn("User id={} has no memberships in any program", user.getId());
            throw new ForbiddenOperationException("User id=" + user.getId() + " has no memberships");
        }
        if (memberships.size() > 1) {
            log.warn("Ambiguous program resolution: User id={} belongs to {} programs", user.getId(), memberships.size());
            throw new InvalidRequestException(
                    "User belongs to multiple programs; programId must be specified");
        }
        return memberships.getFirst();
    }

    private EarningRule resolveEarningRule(EarnPointsRequest request, LoyaltyProgram program, LocalDateTime now) {
        if (request.earningRuleId() != null) {
            EarningRule rule = earningRuleRepository.findById(request.earningRuleId())
                    .orElseThrow(() -> {
                        log.warn("Earning rule not found: id={}", request.earningRuleId());
                        return new ConflictException("EARNING_RULE_NOT_FOUND",
                                "Earning rule not found: id=" + request.earningRuleId());
                    });
            if (rule.getPeriod() != null && !rule.getPeriod().isActiveAt(now)) {
                log.warn("Earning rule id={} is not active at present time", rule.getId());
                throw new NoEarningRuleException(program.getId(), rule.getEventType().name());
            }
            return rule;
        }

        List<EarningRule> candidates = earningRuleRepository
                .findByProgramIdAndEventType(program.getId(), request.eventType());
        return candidates.stream()
                .filter(r -> r.getPeriod() == null || r.getPeriod().isActiveAt(now))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No active earning rule found for programId={} and eventType={}", program.getId(), request.eventType());
                    return new NoEarningRuleException(program.getId(), request.eventType().name());
                });
    }

    private int applyCampaigns(int basePoints, LoyaltyProgram program, EarningEventType eventType, LocalDateTime now) {
        List<Campaign> activeCampaigns = campaignRepository.findActiveCampaigns(eventType, program.getId(), now);
        if (activeCampaigns.isEmpty()) {
            return basePoints;
        }

        BigDecimal totalMultiplier = activeCampaigns.stream()
                .map(Campaign::getMultiplier)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(BigDecimal.valueOf(activeCampaigns.size() - 1));
        int calculatedPoints = BigDecimal.valueOf(basePoints)
                .multiply(totalMultiplier)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        log.debug("Applied {} active campaigns for programId={}. Base points: {}, Multiplied points: {}",
                activeCampaigns.size(), program.getId(), basePoints, calculatedPoints);
        return calculatedPoints;
    }
}