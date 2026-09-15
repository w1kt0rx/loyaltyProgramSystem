package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateRewardRequest;
import com.example.loyaltyprogram.dto.response.RewardResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Reward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

public class RewardMapperTest {
    private RewardMapper rewardMapper;

    @BeforeEach
    void setup() {
        this.rewardMapper = Mappers.getMapper(RewardMapper.class);
    }

    @Test
    void toEntity_validRequest_mapsToRewardEntity() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateRewardRequest request = new CreateRewardRequest("Free Coffee", 500, 100, startDate, endDate);
        // when
        Reward entity = rewardMapper.toEntity(request);
        // then
        Assertions.assertNotNull(entity);
        Assertions.assertAll(
                () -> Assertions.assertNull(entity.getId()),
                () -> Assertions.assertNull(entity.getProgram()),
                () -> Assertions.assertNull(entity.getPeriod()),
                () -> Assertions.assertEquals("Free Coffee", entity.getName()),
                () -> Assertions.assertEquals(500, entity.getPointsCost()),
                () -> Assertions.assertEquals(100, entity.getAvailableQuantity())
        );
    }

    @Test
    void toEntity_nullRequest_returnsNull() {
        // given
        CreateRewardRequest request = null;
        // when
        Reward entity = rewardMapper.toEntity(request);
        // then
        Assertions.assertNull(entity);
    }

    @Test
    void toResponse_validRewardWithProgram_mapsToRewardResponse() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        program.setId(10L);
        Reward entity = new Reward();
        entity.setId(1L);
        entity.setName("Free Mug");
        entity.setPointsCost(300);
        entity.setAvailableQuantity(50);
        entity.setProgram(program);
        // when
        RewardResponse response = rewardMapper.toResponse(entity);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, response.id()),
                () -> Assertions.assertEquals(10L, response.programId()),
                () -> Assertions.assertEquals("Free Mug", response.name()),
                () -> Assertions.assertEquals(300, response.pointsCost()),
                () -> Assertions.assertEquals(50, response.availableQuantity())
        );
    }

    @Test
    void toResponse_rewardWithNullProgram_mapsWithNullProgramId() {
        // given
        Reward entity = new Reward();
        entity.setId(1L);
        entity.setName("Discount Coupon");
        entity.setPointsCost(150);
        entity.setAvailableQuantity(null);
        entity.setProgram(null);
        // when
        RewardResponse response = rewardMapper.toResponse(entity);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, response.id()),
                () -> Assertions.assertNull(response.programId()),
                () -> Assertions.assertEquals("Discount Coupon", response.name()),
                () -> Assertions.assertEquals(150, response.pointsCost()),
                () -> Assertions.assertNull(response.availableQuantity())
        );
    }

    @Test
    void toResponse_nullReward_returnsNull() {
        // given
        Reward entity = null;
        // when
        RewardResponse response = rewardMapper.toResponse(entity);
        // then
        Assertions.assertNull(response);
    }
}