package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.response.BalanceResponse;
import com.example.loyaltyprogram.model.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = LoyaltyMapperConfig.class)
public interface MembershipMapper {
    @Mapping(target = "membershipId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "programId", source = "program.id")
    @Mapping(target = "programName", source = "program.name")
    BalanceResponse toBalanceResponse(Membership membership);
}
