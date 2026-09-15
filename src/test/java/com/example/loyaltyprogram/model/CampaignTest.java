package com.example.loyaltyprogram.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class CampaignTest {

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        Campaign campaign1 = new Campaign();
        campaign1.setId(10L);
        Campaign campaign2 = new Campaign();
        campaign2.setId(10L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(campaign1, campaign2),
                () -> Assertions.assertEquals(campaign1.hashCode(), campaign2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        Campaign campaign1 = new Campaign();
        campaign1.setId(10L);
        Campaign campaign2 = new Campaign();
        campaign2.setId(20L);
        Campaign campaign3 = new Campaign();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(campaign1, campaign2),
                () -> Assertions.assertNotEquals(campaign1, campaign3),
                () -> Assertions.assertNotEquals(null, campaign1),
                () -> Assertions.assertNotEquals(new Object(), campaign1)
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        Campaign campaign = new Campaign();
        Set<Campaign> campaigns = new HashSet<>();
        campaigns.add(campaign);
        int hashBeforeSave = campaign.hashCode();
        //when
        campaign.setId(42L);
        int hashAfterSave = campaign.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(campaigns.contains(campaign))
        );
    }
}