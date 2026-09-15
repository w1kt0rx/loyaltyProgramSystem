package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateRewardRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class RewardTest {

    @Test
    void update_validRequest_updatesRewardFields() {
        // given
        Reward reward = new Reward();
        reward.setName("Old Name");
        reward.setPointsCost(100);
        reward.setAvailableQuantity(10);
        UpdateRewardRequest request = new UpdateRewardRequest("New Name", 200, 50);
        // when
        Reward updatedReward = reward.update(request);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("New Name", reward.getName()),
                () -> Assertions.assertEquals(200, reward.getPointsCost()),
                () -> Assertions.assertEquals(50, reward.getAvailableQuantity()),
                () -> Assertions.assertEquals(reward, updatedReward)
        );
    }

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        Reward reward1 = new Reward();
        reward1.setId(1L);
        Reward reward2 = new Reward();
        reward2.setId(1L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(reward1, reward2),
                () -> Assertions.assertEquals(reward1.hashCode(), reward2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        Reward reward1 = new Reward();
        reward1.setId(1L);
        Reward reward2 = new Reward();
        reward2.setId(2L);
        Reward reward3 = new Reward();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(reward1, reward2),
                () -> Assertions.assertNotEquals(reward1, reward3),
                () -> Assertions.assertNotEquals(null, reward1),
                () -> Assertions.assertNotEquals(reward1, new Object())
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        Reward reward = new Reward();
        Set<Reward> rewards = new HashSet<>();
        rewards.add(reward);
        int hashBeforeSave = reward.hashCode();
        //when
        reward.setId(42L);
        int hashAfterSave = reward.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(rewards.contains(reward))
        );
    }
}