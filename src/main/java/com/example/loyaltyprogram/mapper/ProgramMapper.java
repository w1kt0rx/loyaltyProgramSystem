package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.response.ProgramResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = LoyaltyMapperConfig.class)
public interface ProgramMapper {
    @Mapping(target = "startDate", source = "period.startDate")
    @Mapping(target = "endDate", source = "period.endDate")
    ProgramResponse toResponse(LoyaltyProgram program);
}
