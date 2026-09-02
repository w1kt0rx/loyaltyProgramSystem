package com.example.loyaltyprogram.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EntityHashSetTest {

    @Test
    void entityRemainsFindableInHashSetAcrossIdAssignment() {
        User user = new User();
        user.setEmail("a@a.pl");
        Set<User> set = new HashSet<>();
        set.add(user);
        assertThat(set.contains(user)).isTrue();
        user.setId(42L);
        assertThat(set.contains(user)).isTrue();
    }
}