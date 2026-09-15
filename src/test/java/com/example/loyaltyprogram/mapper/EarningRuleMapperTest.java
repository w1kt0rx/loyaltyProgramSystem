package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateEarningRuleRequest;
import com.example.loyaltyprogram.dto.response.EarningRuleResponse;
import com.example.loyaltyprogram.model.EarningEventType;
import com.example.loyaltyprogram.model.EarningRule;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Period;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

public class EarningRuleMapperTest {
    private EarningRuleMapper earningRuleMapper;

    @BeforeEach
    void setup() {
        this.earningRuleMapper = Mappers.getMapper(EarningRuleMapper.class);
    }

    @Test
    void toEntity_validRequest_mapsToEarningRuleEntity() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateEarningRuleRequest request = new CreateEarningRuleRequest(EarningEventType.PURCHASE, 100, startDate, endDate);
        // when
        EarningRule entity = earningRuleMapper.toEntity(request);
        // then
        Assertions.assertNotNull(entity);
        Assertions.assertAll(
                () -> Assertions.assertNull(entity.getId()),
                () -> Assertions.assertNull(entity.getProgram()),
                () -> Assertions.assertNull(entity.getPeriod()),
                () -> Assertions.assertEquals(EarningEventType.PURCHASE, entity.getEventType()),
                () -> Assertions.assertEquals(100, entity.getPoints())
        );
    }

    @Test
    void toEntity_nullRequest_returnsNull() {
        // given
        CreateEarningRuleRequest request = null;
        // when
        EarningRule entity = earningRuleMapper.toEntity(request);
        // then
        Assertions.assertNull(entity);
    }

    @Test
    void toResponse_validEntityWithProgramAndPeriod_mapsToEarningRuleResponse() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        LoyaltyProgram program = new LoyaltyProgram();
        program.setId(5L);
        Period period = new Period();
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        EarningRule entity = new EarningRule();
        entity.setId(10L);
        entity.setProgram(program);
        entity.setEventType(EarningEventType.REVIEW);
        entity.setPoints(50);
        entity.setPeriod(period);
        // when
        EarningRuleResponse response = earningRuleMapper.toResponse(entity);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(10L, response.id()),
                () -> Assertions.assertEquals(5L, response.programId()),
                () -> Assertions.assertEquals(EarningEventType.REVIEW, response.eventType()),
                () -> Assertions.assertEquals(50, response.points()),
                () -> Assertions.assertEquals(startDate, response.startDate()),
                () -> Assertions.assertEquals(endDate, response.endDate())
        );
    }

    @Test
    void toResponse_entityWithNullProgramAndNullPeriod_mapsWithNullsInNestedFields() {
        // given
        EarningRule entity = new EarningRule();
        entity.setId(10L);
        entity.setEventType(EarningEventType.REFERRAL);
        entity.setPoints(200);
        entity.setProgram(null);
        entity.setPeriod(null);
        // when
        EarningRuleResponse response = earningRuleMapper.toResponse(entity);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(10L, response.id()),
                () -> Assertions.assertNull(response.programId()),
                () -> Assertions.assertEquals(EarningEventType.REFERRAL, response.eventType()),
                () -> Assertions.assertEquals(200, response.points()),
                () -> Assertions.assertNull(response.startDate()),
                () -> Assertions.assertNull(response.endDate())
        );
    }

    @Test
    void toResponse_nullEntity_returnsNull() {
        // given
        EarningRule entity = null;
        // when
        EarningRuleResponse response = earningRuleMapper.toResponse(entity);
        // then
        Assertions.assertNull(response);
    }
}