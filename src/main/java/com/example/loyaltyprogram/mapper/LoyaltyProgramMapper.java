package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateProgramRequest;
import com.example.loyaltyprogram.dto.response.ProgramResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = LoyaltyMapperConfig.class)
public interface LoyaltyProgramMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    @Mapping(target = "earningRules", ignore = true)
    @Mapping(target = "update", ignore = true)
    @Mapping(target = "rewards", ignore = true)
    @Mapping(source = "startDate", target = "period.startDate")
    @Mapping(source = "endDate", target = "period.endDate")
    LoyaltyProgram toEntity(CreateProgramRequest request);


    @Mapping(target = "startDate", source = "period.startDate")
    @Mapping(target = "endDate", source = "period.endDate")
    ProgramResponse toResponse(LoyaltyProgram program);
}
