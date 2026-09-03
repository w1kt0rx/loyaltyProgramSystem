package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.exception.IllegalDateException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Period {
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;

    public Period(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            throw new IllegalDateException("startDate must not be null");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalDateException("endDate must not be before startDate");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    boolean isActiveAt(LocalDateTime moment) {
        if(moment == null) {
            throw new IllegalDateException("Moment must not be null");
        }
        boolean afterOrEqualStart = !moment.isBefore(startDate);
        boolean beforeOrEqualEnd = (endDate == null) || !moment.isAfter(endDate);
        return afterOrEqualStart && beforeOrEqualEnd;
    }

    public boolean overlaps(Period other) {
        LocalDateTime thisEnd = this.endDate;
        LocalDateTime otherEnd = other.endDate;
        boolean startsBeforeOtherEnds = otherEnd == null || !this.startDate.isAfter(otherEnd);
        boolean endsAfterOtherStarts = thisEnd == null || !thisEnd.isBefore(other.startDate);
        return startsBeforeOtherEnds && endsAfterOtherStarts;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Period period)) return false;
        return Objects.equals(startDate, period.startDate) && Objects.equals(endDate, period.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
