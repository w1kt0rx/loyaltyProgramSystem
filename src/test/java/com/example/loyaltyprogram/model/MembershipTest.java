package com.example.loyaltyprogram.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class MembershipTest {

    @Test
    void constructor_default_setsJoinDateToCurrentDateAndPointsBalanceToZero() {
        // given + when
        Membership membership = new Membership();
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(LocalDate.now(), membership.getJoinDate()),
                () -> Assertions.assertEquals(0, membership.getPointsBalance()),
                () -> Assertions.assertNotNull(membership.getTransactions()),
                () -> Assertions.assertTrue(membership.getTransactions().isEmpty())
        );
    }

    @Test
    void addTransaction_validTransaction_addsToListAndSetsMembershipReference() {
        // given
        Membership membership = new Membership();
        PointsTransaction transaction = new PointsTransaction();
        // when
        membership.addTransaction(transaction);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, membership.getTransactions().size()),
                () -> Assertions.assertTrue(membership.getTransactions().contains(transaction)),
                () -> Assertions.assertEquals(membership, transaction.getMembership())
        );
    }

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        Membership membership1 = new Membership();
        membership1.setId(10L);
        Membership membership2 = new Membership();
        membership2.setId(10L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(membership1, membership2),
                () -> Assertions.assertEquals(membership1.hashCode(), membership2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        Membership membership1 = new Membership();
        membership1.setId(10L);
        Membership membership2 = new Membership();
        membership2.setId(20L);
        Membership membership3 = new Membership();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(membership1, membership2),
                () -> Assertions.assertNotEquals(membership1, membership3),
                () -> Assertions.assertNotEquals(null, membership1),
                () -> Assertions.assertNotEquals(new Object(), membership1)
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        Membership membership = new Membership();
        Set<Membership> memberships = new HashSet<>();
        memberships.add(membership);
        int hashBeforeSave = membership.hashCode();
        //when
        membership.setId(42L);
        int hashAfterSave = membership.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(memberships.contains(membership))
        );
    }
}