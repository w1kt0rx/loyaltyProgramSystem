package com.example.loyaltyprogram.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class PointsTransactionTest {

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        PointsTransaction transaction1 = new PointsTransaction();
        transaction1.setId(100L);
        PointsTransaction transaction2 = new PointsTransaction();
        transaction2.setId(100L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(transaction1, transaction2),
                () -> Assertions.assertEquals(transaction1.hashCode(), transaction2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        PointsTransaction transaction1 = new PointsTransaction();
        transaction1.setId(100L);
        PointsTransaction transaction2 = new PointsTransaction();
        transaction2.setId(200L);
        PointsTransaction transaction3 = new PointsTransaction();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(transaction1, transaction2),
                () -> Assertions.assertNotEquals(transaction1, transaction3),
                () -> Assertions.assertNotEquals(null, transaction1),
                () -> Assertions.assertNotEquals(new Object(), transaction1)
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        PointsTransaction pointsTransaction = new PointsTransaction();
        Set<PointsTransaction> pointsTransactions = new HashSet<>();
        pointsTransactions.add(pointsTransaction);
        int hashBeforeSave = pointsTransaction.hashCode();
        //when
        pointsTransaction.setId(42L);
        int hashAfterSave = pointsTransaction.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(pointsTransactions.contains(pointsTransaction))
        );
    }
}