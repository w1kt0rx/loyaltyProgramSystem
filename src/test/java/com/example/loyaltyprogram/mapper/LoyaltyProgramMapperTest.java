package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateProgramRequest;
import com.example.loyaltyprogram.dto.response.ProgramResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Period;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

public class LoyaltyProgramMapperTest {
    private LoyaltyProgramMapper loyaltyProgramMapper;

    @BeforeEach
    void setup() {
        this.loyaltyProgramMapper = Mappers.getMapper(LoyaltyProgramMapper.class);
    }

    @Test
    void toEntity_validRequest_mapsToLoyaltyProgramEntity() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateProgramRequest request = new CreateProgramRequest("VIP Club", "Exclusive VIP loyalty program", startDate, endDate);
        // when
        LoyaltyProgram entity = loyaltyProgramMapper.toEntity(request);
        // then
        Assertions.assertNotNull(entity);
        Assertions.assertAll(
                () -> Assertions.assertNull(entity.getId()),
                () -> Assertions.assertEquals("VIP Club", entity.getName()),
                () -> Assertions.assertEquals("Exclusive VIP loyalty program", entity.getDescription()),
                () -> Assertions.assertNotNull(entity.getPeriod()),
                () -> Assertions.assertEquals(startDate, entity.getPeriod().getStartDate()),
                () -> Assertions.assertEquals(endDate, entity.getPeriod().getEndDate()),
                () -> Assertions.assertTrue(entity.getMemberships().isEmpty()),
                () -> Assertions.assertTrue(entity.getEarningRules().isEmpty()),
                () -> Assertions.assertTrue(entity.getRewards().isEmpty())
        );
    }

    @Test
    void toEntity_nullRequest_returnsNull() {
        // given
        CreateProgramRequest request = null;
        // when
        LoyaltyProgram entity = loyaltyProgramMapper.toEntity(request);
        // then
        Assertions.assertNull(entity);
    }

    @Test
    void toResponse_validProgramWithPeriod_mapsToProgramResponse() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        Period period = new Period();
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        LoyaltyProgram entity = new LoyaltyProgram();
        entity.setId(10L);
        entity.setName("Gold Club");
        entity.setDescription("Gold level rewards");
        entity.setPeriod(period);
        // when
        ProgramResponse response = loyaltyProgramMapper.toResponse(entity);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(10L, response.id()),
                () -> Assertions.assertEquals("Gold Club", response.name()),
                () -> Assertions.assertEquals("Gold level rewards", response.description()),
                () -> Assertions.assertEquals(startDate, response.startDate()),
                () -> Assertions.assertEquals(endDate, response.endDate())
        );
    }

    @Test
    void toResponse_programWithNullPeriod_mapsWithNullDates() {
        // given
        LoyaltyProgram entity = new LoyaltyProgram();
        entity.setId(11L);
        entity.setName("Silver Club");
        entity.setDescription("Silver level rewards");
        entity.setPeriod(null);
        // when
        ProgramResponse response = loyaltyProgramMapper.toResponse(entity);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(11L, response.id()),
                () -> Assertions.assertEquals("Silver Club", response.name()),
                () -> Assertions.assertEquals("Silver level rewards", response.description()),
                () -> Assertions.assertNull(response.startDate()),
                () -> Assertions.assertNull(response.endDate())
        );
    }

    @Test
    void toResponse_nullProgram_returnsNull() {
        // given
        LoyaltyProgram program = null;
        // when
        ProgramResponse response = loyaltyProgramMapper.toResponse(program);
        // then
        Assertions.assertNull(response);
    }
}