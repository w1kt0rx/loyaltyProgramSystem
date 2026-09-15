package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateUserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class UserTest {

    @Test
    void addMembership_validMembership_addsToListAndSetsUserReference() {
        // given
        User user = new User();
        Membership membership = new Membership();
        // when
        user.addMembership(membership);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, user.getMemberships().size()),
                () -> Assertions.assertTrue(user.getMemberships().contains(membership)),
                () -> Assertions.assertEquals(user, membership.getUser())
        );
    }

    @Test
    void removeMembership_existingMembership_removesFromListAndClearsUserReference() {
        // given
        User user = new User();
        Membership membership = new Membership();
        user.addMembership(membership);
        // when
        user.removeMembership(membership);
        // then
        Assertions.assertAll(
                () -> Assertions.assertTrue(user.getMemberships().isEmpty()),
                () -> Assertions.assertNull(membership.getUser())
        );
    }

    @Test
    void update_validRequest_updatesFirstAndLastName() {
        // given
        User user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        UpdateUserRequest request = new UpdateUserRequest("Adam", "Nowak");
        // when
        User updatedUser = user.update(request);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Adam", user.getFirstName()),
                () -> Assertions.assertEquals("Nowak", user.getLastName()),
                () -> Assertions.assertEquals(user, updatedUser)
        );
    }

    @Test
    void deactivate_defaultUser_setsDeactivatedToTrue() {
        // given
        User user = new User();
        // when
        user.deactivate();
        // then
        Assertions.assertTrue(user.isDeactivated());
    }

    @Test
    void equalsAndHashCode_sameId_returnsTrueAndSameHashCode() {
        // given
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(1L);
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user1, user2),
                () -> Assertions.assertEquals(user1.hashCode(), user2.hashCode())
        );
    }

    @Test
    void equals_differentIdOrNullId_returnsFalse() {
        // given
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        // when + then
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(user1, user2),
                () -> Assertions.assertNotEquals(user1, user3),
                () -> Assertions.assertNotEquals(null, user1),
                () -> Assertions.assertNotEquals(new Object(), user1)
        );
    }

    @Test
    void hashCode_remainsStable_afterIdIsAssignedByPersistenceContext() {
        //given
        User user = new User();
        Set<User> users = new HashSet<>();
        users.add(user);
        int hashBeforeSave = user.hashCode();
        //when
        user.setId(42L);
        int hashAfterSave = user.hashCode();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(hashBeforeSave, hashAfterSave),
                () -> Assertions.assertTrue(users.contains(user))
        );
    }
}