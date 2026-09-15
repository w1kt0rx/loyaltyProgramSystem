package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateProgramRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class LoyaltyProgramTest {

    @Test
    void addMembership_validMembership_addsToListAndSetsProgramReference() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        Membership membership = new Membership();
        // when
        program.addMembership(membership);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, program.getMemberships().size()),
                () -> Assertions.assertTrue(program.getMemberships().contains(membership)),
                () -> Assertions.assertEquals(program, membership.getProgram())
        );
    }

    @Test
    void addEarningRule_validEarningRule_addsToListAndSetsProgramReference() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        EarningRule earningRule = new EarningRule();
        // when
        program.addEarningRule(earningRule);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, program.getEarningRules().size()),
                () -> Assertions.assertTrue(program.getEarningRules().contains(earningRule)),
                () -> Assertions.assertEquals(program, earningRule.getProgram())
        );
    }

    @Test
    void removeEarningRule_existingEarningRule_removesFromListAndClearsProgramReference() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        EarningRule earningRule = new EarningRule();
        program.addEarningRule(earningRule);
        // when
        program.removeEarningRule(earningRule);
        // then
        Assertions.assertAll(
                () -> Assertions.assertTrue(program.getEarningRules().isEmpty()),
                () -> Assertions.assertNull(earningRule.getProgram())
        );
    }

    @Test
    void addReward_validReward_addsToListAndSetsProgramReference() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        Reward reward = new Reward();
        // when
        program.addReward(reward);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, program.getRewards().size()),
                () -> Assertions.assertTrue(program.getRewards().contains(reward)),
                () -> Assertions.assertEquals(program, reward.getProgram())
        );
    }

    @Test
    void removeReward_existingReward_removesFromListAndClearsProgramReference() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        Reward reward = new Reward();
        program.addReward(reward);
        // when
        program.removeReward(reward);
        // then
        Assertions.assertAll(
                () -> Assertions.assertTrue(program.getRewards().isEmpty()),
                () -> Assertions.assertNull(reward.getProgram())
        );
    }

    @Test
    void isActiveAt_validPeriod_delegatesToPeriodIsActiveAt() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Period period = new Period();
        period.setStartDate(now.minusDays(1));
        period.setEndDate(now.plusDays(1));
        LoyaltyProgram program = new LoyaltyProgram();
        program.setPeriod(period);
        // when
        boolean active = program.isActiveAt(now);
        // then
        Assertions.assertTrue(active);
    }

    @Test
    void update_validRequest_updatesNameAndDescription() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        program.setName("Old Name");
        program.setDescription("Old Description");
        UpdateProgramRequest request = new UpdateProgramRequest("New Name", "New Description");
        // when
        LoyaltyProgram updatedProgram = program.update(request);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("New Name", program.getName()),
                () -> Assertions.assertEquals("New Description", program.getDescription()),
                () -> Assertions.assertEquals(program, updatedProgram)
        );
    }

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        LoyaltyProgram program1 = new LoyaltyProgram();
        program1.setId(10L);
        LoyaltyProgram program2 = new LoyaltyProgram();
        program2.setId(10L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(program1, program2),
                () -> Assertions.assertEquals(program1.hashCode(), program2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        LoyaltyProgram program1 = new LoyaltyProgram();
        program1.setId(10L);
        LoyaltyProgram program2 = new LoyaltyProgram();
        program2.setId(20L);
        LoyaltyProgram program3 = new LoyaltyProgram();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(program1, program2),
                () -> Assertions.assertNotEquals(program1, program3),
                () -> Assertions.assertNotEquals(null, program1),
                () -> Assertions.assertNotEquals(new Object(), program1)
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
        Set<LoyaltyProgram> loyaltyPrograms = new HashSet<>();
        loyaltyPrograms.add(loyaltyProgram);
        int hashBeforeSave = loyaltyProgram.hashCode();
        //when
        loyaltyProgram.setId(42L);
        int hashAfterSave = loyaltyProgram.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(loyaltyPrograms.contains(loyaltyProgram))
        );
    }
}