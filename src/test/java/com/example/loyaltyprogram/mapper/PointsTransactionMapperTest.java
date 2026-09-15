package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.response.EarnPointsResponse;
import com.example.loyaltyprogram.dto.response.PointsHistoryResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.PointsTransaction;
import com.example.loyaltyprogram.model.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

public class PointsTransactionMapperTest {

    private PointsTransactionMapper pointsTransactionMapper;

    @BeforeEach
    void setup() {
        this.pointsTransactionMapper = Mappers.getMapper(PointsTransactionMapper.class);
    }

    @Test
    void toHistoryResponse_validTransaction_mapsToPointsHistoryResponse() {
        // given
        LocalDateTime occurredAt = LocalDateTime.of(2026, 3, 1, 12, 0);
        LoyaltyProgram program = new LoyaltyProgram();
        program.setName("Gold Club");
        Membership membership = new Membership();
        membership.setProgram(program);
        PointsTransaction transaction = new PointsTransaction();
        transaction.setId(100L);
        transaction.setType(TransactionType.EARN);
        transaction.setPoints(150);
        transaction.setDescription("Earned for purchase");
        transaction.setOccurredAt(occurredAt);
        transaction.setMembership(membership);
        // when
        PointsHistoryResponse response = pointsTransactionMapper.toHistoryResponse(transaction);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(100L, response.id()),
                () -> Assertions.assertEquals("EARN", response.type()),
                () -> Assertions.assertEquals(150, response.points()),
                () -> Assertions.assertEquals("Earned for purchase", response.description()),
                () -> Assertions.assertEquals("Gold Club", response.programName()),
                () -> Assertions.assertEquals(occurredAt, response.occurredAt())
        );
    }

    @Test
    void toHistoryResponse_transactionWithNullMembershipAndProgram_mapsWithNullProgramName() {
        // given
        LocalDateTime occurredAt = LocalDateTime.of(2026, 3, 1, 12, 0);
        PointsTransaction transaction = new PointsTransaction();
        transaction.setId(101L);
        transaction.setType(TransactionType.REDEEM);
        transaction.setPoints(50);
        transaction.setDescription("Redeemed reward");
        transaction.setOccurredAt(occurredAt);
        transaction.setMembership(null);
        // when
        PointsHistoryResponse response = pointsTransactionMapper.toHistoryResponse(transaction);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(101L, response.id()),
                () -> Assertions.assertEquals("REDEEM", response.type()),
                () -> Assertions.assertEquals(50, response.points()),
                () -> Assertions.assertEquals("Redeemed reward", response.description()),
                () -> Assertions.assertNull(response.programName()),
                () -> Assertions.assertEquals(occurredAt, response.occurredAt())
        );
    }

    @Test
    void toHistoryResponse_nullTransaction_returnsNull() {
        // given
        PointsTransaction transaction = null;
        // when
        PointsHistoryResponse response = pointsTransactionMapper.toHistoryResponse(transaction);
        // then
        Assertions.assertNull(response);
    }

    @Test
    void toEarnResponse_validTransaction_mapsToEarnPointsResponse() {
        // given
        Membership membership = new Membership();
        membership.setPointsBalance(500);
        PointsTransaction transaction = new PointsTransaction();
        transaction.setId(200L);
        transaction.setType(TransactionType.EARN);
        transaction.setPoints(100);
        transaction.setReferenceId("REF-999");
        transaction.setMembership(membership);
        // when
        EarnPointsResponse response = pointsTransactionMapper.toEarnResponse(transaction);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(200L, response.transactionId()),
                () -> Assertions.assertEquals("EARN", response.eventType()),
                () -> Assertions.assertEquals(100, response.pointsEarned()),
                () -> Assertions.assertEquals(500, response.newBalance()),
                () -> Assertions.assertEquals("REF-999", response.referenceId())
        );
    }

    @Test
    void toEarnResponse_transactionWithNullMembership_mapsWithZeroNewBalance() {
        // given
        PointsTransaction transaction = new PointsTransaction();
        transaction.setId(201L);
        transaction.setType(TransactionType.EARN);
        transaction.setPoints(50);
        transaction.setReferenceId("REF-888");
        transaction.setMembership(null);
        // when
        EarnPointsResponse response = pointsTransactionMapper.toEarnResponse(transaction);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(201L, response.transactionId()),
                () -> Assertions.assertEquals("EARN", response.eventType()),
                () -> Assertions.assertEquals(50, response.pointsEarned()),
                () -> Assertions.assertEquals(0, response.newBalance()),
                () -> Assertions.assertEquals("REF-888", response.referenceId())
        );
    }

    @Test
    void toEarnResponse_nullTransaction_returnsNull() {
        // given
        PointsTransaction transaction = null;
        // when
        EarnPointsResponse response = pointsTransactionMapper.toEarnResponse(transaction);
        // then
        Assertions.assertNull(response);
    }
}