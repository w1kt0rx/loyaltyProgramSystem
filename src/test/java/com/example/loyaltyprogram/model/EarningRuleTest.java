package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateEarningRuleRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EarningRuleTest {

    @Test
    void update_validRequest_updatesPeriodAndPoints() {
        // given
        LocalDateTime oldStart = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime oldEnd = LocalDateTime.of(2026, 6, 30, 23, 59);
        Period period = new Period();
        period.setStartDate(oldStart);
        period.setEndDate(oldEnd);
        EarningRule earningRule = new EarningRule();
        earningRule.setPeriod(period);
        earningRule.setPoints(50);
        LocalDateTime newStart = LocalDateTime.of(2026, 7, 1, 0, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 12, 31, 23, 59);
        UpdateEarningRuleRequest request = new UpdateEarningRuleRequest(null, 100, newStart, newEnd);
        // when
        EarningRule updatedEarningRule = earningRule.applyUpdate(request);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(100, earningRule.getPoints()),
                () -> Assertions.assertEquals(newStart, earningRule.getPeriod().getStartDate()),
                () -> Assertions.assertEquals(newEnd, earningRule.getPeriod().getEndDate()),
                () -> Assertions.assertEquals(earningRule, updatedEarningRule)
        );
    }

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        EarningRule rule1 = new EarningRule();
        rule1.setId(10L);
        EarningRule rule2 = new EarningRule();
        rule2.setId(10L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(rule1, rule2),
                () -> Assertions.assertEquals(rule1.hashCode(), rule2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        EarningRule rule1 = new EarningRule();
        rule1.setId(10L);
        EarningRule rule2 = new EarningRule();
        rule2.setId(20L);
        EarningRule rule3 = new EarningRule();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(rule1, rule2),
                () -> Assertions.assertNotEquals(rule1, rule3),
                () -> Assertions.assertNotEquals(null, rule1),
                () -> Assertions.assertNotEquals(new Object(), rule1)
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        EarningRule earningRule = new EarningRule();
        Set<EarningRule> earningRules = new HashSet<>();
        earningRules.add(earningRule);
        int hashBeforeSave = earningRule.hashCode();
        //when
        earningRule.setId(42L);
        int hashAfterSave = earningRule.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(earningRules.contains(earningRule))
        );
    }
}