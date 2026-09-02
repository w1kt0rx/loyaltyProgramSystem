package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.response.PointsHistoryResponse;
import com.example.loyaltyprogram.model.PointsTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = LoyaltyMapperConfig.class)
public interface PointsTransactionMapper {

    @Mapping(target = "type", expression = "java(transaction.getType().name())")
    @Mapping(target = "programName", source = "membership.program.name")
    PointsHistoryResponse toHistoryResponse(PointsTransaction transaction);
}
