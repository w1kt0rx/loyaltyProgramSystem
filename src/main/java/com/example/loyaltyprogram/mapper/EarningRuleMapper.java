package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateEarningRuleRequest;
import com.example.loyaltyprogram.dto.response.EarningRuleResponse;
import com.example.loyaltyprogram.model.EarningRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = LoyaltyMapperConfig.class)
public interface EarningRuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "period.startDate", ignore = true)
    @Mapping(target = "period.endDate", ignore = true)
    @Mapping(target = "update", ignore = true)
    EarningRule toEntity(CreateEarningRuleRequest request);

    @Mapping(source = "period.startDate", target = "startDate")
    @Mapping(source = "period.endDate", target = "endDate")
    @Mapping(source = "program.id", target = "programId")
    EarningRuleResponse toResponse(EarningRule earningRule);
}
