package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateEarningRuleRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "earning_rule")
@Entity
public class EarningRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private LoyaltyProgram program;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EarningEventType eventType;
    @Column(nullable = false)
    private int points;
    @Embedded
    private Period period;

    public EarningRule update(UpdateEarningRuleRequest request) {
        period.setStartDate(request.startDate());
        period.setEndDate(request.endDate());
        this.points = request.points();
        return this;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EarningRule))
            return false;
        EarningRule other = (EarningRule) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
