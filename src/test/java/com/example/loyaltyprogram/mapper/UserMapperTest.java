package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.response.UserResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toResponse_flattensProgramSummaries_withoutNestedTransactions() {
        User user = new User();
        user.setId(1L);
        user.setEmail("jan@example.com");
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        LoyaltyProgram program = new LoyaltyProgram();
        program.setId(10L);
        program.setName("Gold");
        Membership membership = new Membership();
        membership.setUser(user);
        membership.setProgram(program);
        user.setMemberships(List.of(membership));
        UserResponse response = mapper.toResponse(user);
        assertThat(response.programs()).hasSize(1);
        assertThat(response.programs().getFirst().id()).isEqualTo(10L);
        assertThat(response.programs().getFirst().name()).isEqualTo("Gold");
    }
}
