package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.exception.IllegalDateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeriodTest {

    @Test
    void isActiveAt_returnsTrue_onStartBoundary_inclusive() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        Period period = new Period(start, end);
        // When
        boolean active = period.isActiveAt(start);
        // Then
        assertThat(active).isTrue();
    }

    @Test
    void isActiveAt_returnsTrue_onEndBoundary_inclusive() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        Period period = new Period(start, end);
        // When
        boolean active = period.isActiveAt(end);
        // Then
        assertThat(active).isTrue();
    }

    @Test
    void isActiveAt_returnsFalse_justBeforeStart() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        Period period = new Period(start, end);
        // When
        boolean active = period.isActiveAt(start.minusNanos(1));
        // Then
        assertThat(active).isFalse();
    }

    @Test
    void isActiveAt_returnsFalse_justAfterEnd() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        Period period = new Period(start, end);
        // When
        boolean active = period.isActiveAt(end.plusNanos(1));
        // Then
        assertThat(active).isFalse();
    }

    @Test
    void isActiveAt_returnsTrue_forAnyFutureMoment_whenEndDateIsNull() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        Period unbounded = new Period(start, null);
        // When
        boolean active = unbounded.isActiveAt(start.plusYears(50));
        // Then
        assertThat(active).isTrue();
    }

    @Test
    void isActiveAt_throwsIllegalDateException_whenMomentIsNull() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        Period period = new Period(start, end);
        // When & Then
        assertThatThrownBy(() -> period.isActiveAt(null))
                .isInstanceOf(IllegalDateException.class);
    }

    @Test
    void constructor_throwsIllegalDateException_whenStartDateIsNull() {
        // Given
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        // When & Then
        assertThatThrownBy(() -> new Period(null, end))
                .isInstanceOf(IllegalDateException.class);
    }

    @Test
    void constructor_throwsIllegalDateException_whenEndDateBeforeStartDate() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);
        // When & Then
        assertThatThrownBy(() -> new Period(end, start))
                .isInstanceOf(IllegalDateException.class);
    }

    @Test
    void constructor_allowsNullEndDate_meaningUnbounded() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        // When
        Period period = new Period(start, null);
        // Then
        assertThat(period.getEndDate()).isNull();
    }
}