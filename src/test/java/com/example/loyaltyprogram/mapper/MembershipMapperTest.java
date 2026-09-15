package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.response.BalanceResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class MembershipMapperTest {
    private MembershipMapper membershipMapper;

    @BeforeEach
    void setup() {
        this.membershipMapper = Mappers.getMapper(MembershipMapper.class);
    }

    @Test
    void toBalanceResponse_validMembership_mapsToBalanceResponse() {
        // given
        User user = new User();
        user.setId(10L);
        LoyaltyProgram program = new LoyaltyProgram();
        program.setId(20L);
        program.setName("Gold Club");
        Membership membership = new Membership();
        membership.setId(100L);
        membership.setUser(user);
        membership.setProgram(program);
        membership.setPointsBalance(350);
        // when
        BalanceResponse response = membershipMapper.toBalanceResponse(membership);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(100L, response.membershipId()),
                () -> Assertions.assertEquals(10L, response.userId()),
                () -> Assertions.assertEquals(20L, response.programId()),
                () -> Assertions.assertEquals("Gold Club", response.programName()),
                () -> Assertions.assertEquals(350, response.pointsBalance())
        );
    }

    @Test
    void toBalanceResponse_membershipWithNullUserAndProgram_mapsWithNullNestedFields() {
        // given
        Membership membership = new Membership();
        membership.setId(101L);
        membership.setUser(null);
        membership.setProgram(null);
        membership.setPointsBalance(100);
        // when
        BalanceResponse response = membershipMapper.toBalanceResponse(membership);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(101L, response.membershipId()),
                () -> Assertions.assertNull(response.userId()),
                () -> Assertions.assertNull(response.programId()),
                () -> Assertions.assertNull(response.programName()),
                () -> Assertions.assertEquals(100, response.pointsBalance())
        );
    }

    @Test
    void toBalanceResponse_nullMembership_returnsNull() {
        // given
        Membership membership = null;
        // when
        BalanceResponse response = membershipMapper.toBalanceResponse(membership);
        // then
        Assertions.assertNull(response);
    }
}