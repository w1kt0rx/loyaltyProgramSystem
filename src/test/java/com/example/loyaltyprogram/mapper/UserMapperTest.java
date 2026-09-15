package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.CreateUserRequest;
import com.example.loyaltyprogram.dto.response.ProgramSummaryResponse;
import com.example.loyaltyprogram.dto.response.UserResponse;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class UserMapperTest {
    private UserMapper userMapper;

    @BeforeEach
    void setup() {
        this.userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toEntity_validRequest_mapsToUserEntity() {
        // given
        CreateUserRequest request = new CreateUserRequest("john.doe@example.com", "John", "Doe", 1L);
        // when
        User entity = userMapper.toEntity(request);
        // then
        Assertions.assertNotNull(entity);
        Assertions.assertAll(
                () -> Assertions.assertNull(entity.getId()),
                () -> Assertions.assertEquals("john.doe@example.com", entity.getEmail()),
                () -> Assertions.assertEquals("John", entity.getFirstName()),
                () -> Assertions.assertEquals("Doe", entity.getLastName()),
                () -> Assertions.assertFalse(entity.isDeactivated()),
                () -> Assertions.assertTrue(entity.getMemberships().isEmpty())
        );
    }

    @Test
    void toEntity_nullRequest_returnsNull() {
        // given
        CreateUserRequest request = null;
        // when
        User entity = userMapper.toEntity(request);
        // then
        Assertions.assertNull(entity);
    }

    @Test
    void toResponse_validUserWithMemberships_mapsToUserResponse() {
        // given
        LoyaltyProgram program1 = new LoyaltyProgram();
        program1.setId(10L);
        program1.setName("Gold Club");
        Membership membership1 = new Membership();
        membership1.setId(100L);
        membership1.setProgram(program1);
        LoyaltyProgram program2 = new LoyaltyProgram();
        program2.setId(20L);
        program2.setName("Silver Club");
        Membership membership2 = new Membership();
        membership2.setId(200L);
        membership2.setProgram(program2);
        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.addMembership(membership1);
        user.addMembership(membership2);
        // when
        UserResponse response = userMapper.toResponse(user);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, response.id()),
                () -> Assertions.assertEquals("john.doe@example.com", response.email()),
                () -> Assertions.assertEquals("John", response.firstName()),
                () -> Assertions.assertEquals("Doe", response.lastName()),
                () -> Assertions.assertEquals(2, response.programs().size()),
                () -> Assertions.assertEquals(10L, response.programs().getFirst().id()),
                () -> Assertions.assertEquals("Gold Club", response.programs().getFirst().name()),
                () -> Assertions.assertEquals(20L, response.programs().get(1).id()),
                () -> Assertions.assertEquals("Silver Club", response.programs().get(1).name())
        );
    }

    @Test
    void toResponse_nullUser_returnsNull() {
        // given
        User user = null;
        // when
        UserResponse response = userMapper.toResponse(user);
        // then
        Assertions.assertNull(response);
    }

    @Test
    void toProgramSummary_validMembershipWithProgram_mapsToProgramSummaryResponse() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        program.setId(5L);
        program.setName("VIP Club");
        Membership membership = new Membership();
        membership.setId(1L);
        membership.setProgram(program);
        // when
        ProgramSummaryResponse response = userMapper.toProgramSummary(membership);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(5L, response.id()),
                () -> Assertions.assertEquals("VIP Club", response.name())
        );
    }

    @Test
    void toProgramSummary_membershipWithNullProgram_mapsWithNullFields() {
        // given
        Membership membership = new Membership();
        membership.setId(1L);
        membership.setProgram(null);
        // when
        ProgramSummaryResponse response = userMapper.toProgramSummary(membership);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertNull(response.id()),
                () -> Assertions.assertNull(response.name())
        );
    }

    @Test
    void toProgramSummary_nullMembership_returnsNull() {
        // given
        Membership membership = null;
        // when
        ProgramSummaryResponse response = userMapper.toProgramSummary(membership);
        // then
        Assertions.assertNull(response);
    }

    @Test
    void toProgramSummaries_validListOfMemberships_mapsToListOfProgramSummaryResponses() {
        // given
        LoyaltyProgram program = new LoyaltyProgram();
        program.setId(1L);
        program.setName("Club A");
        Membership membership = new Membership();
        membership.setProgram(program);
        List<Membership> memberships = List.of(membership);
        // when
        List<ProgramSummaryResponse> responses = userMapper.toProgramSummaries(memberships);
        // then
        Assertions.assertNotNull(responses);
        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(1L, responses.getFirst().id());
        Assertions.assertEquals("Club A", responses.getFirst().name());
    }

    @Test
    void toProgramSummaries_nullList_returnsNull() {
        // given
        List<Membership> memberships = null;
        // when
        List<ProgramSummaryResponse> responses = userMapper.toProgramSummaries(memberships);
        // then
        Assertions.assertNull(responses);
    }
}