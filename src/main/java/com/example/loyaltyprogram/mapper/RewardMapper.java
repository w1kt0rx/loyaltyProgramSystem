package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateRewardRequest;
import com.example.loyaltyprogram.dto.response.RewardResponse;
import com.example.loyaltyprogram.model.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = LoyaltyMapperConfig.class)
public interface RewardMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(source = "startDate", target = "period.startDate")
    @Mapping(source = "endDate", target = "period.endDate")
    @Mapping(target = "update", ignore = true)
    Reward toEntity(CreateRewardRequest request);

    @Mapping(source = "program.id", target = "programId")
    RewardResponse toResponse(Reward reward);
}
