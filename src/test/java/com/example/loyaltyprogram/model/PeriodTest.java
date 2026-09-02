package com.example.loyaltyprogram.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PeriodTest {

    @Test
    void isActiveAt_respectsInclusiveBoundaries() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        Period period = new Period(start, end);

        assertThat(period.isActiveAt(start)).isTrue();
        assertThat(period.isActiveAt(end)).isTrue();
        assertThat(period.isActiveAt(start.minusSeconds(1))).isFalse();
        assertThat(period.isActiveAt(end.plusSeconds(1))).isFalse();
    }

    @Test
    void isActiveAt_nullEndDate_meansUnlimited() {
        Period period = new Period(LocalDateTime.of(2020, 1, 1, 0, 0), null);
        assertThat(period.isActiveAt(LocalDateTime.of(2099, 1, 1, 0, 0))).isTrue();
    }
}