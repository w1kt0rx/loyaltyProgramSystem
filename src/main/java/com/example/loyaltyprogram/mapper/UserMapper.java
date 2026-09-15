package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateUserRequest;
import com.example.loyaltyprogram.dto.response.ProgramSummaryResponse;
import com.example.loyaltyprogram.dto.response.UserResponse;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = LoyaltyMapperConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    @Mapping(target = "update", ignore = true)
    @Mapping(target = "deactivated", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "programs", source = "memberships")
    UserResponse toResponse(User user);

    @Mapping(target = "id", source = "program.id")
    @Mapping(target = "name", source = "program.name")
    ProgramSummaryResponse toProgramSummary(Membership membership);

    List<ProgramSummaryResponse> toProgramSummaries(List<Membership> memberships);
}
