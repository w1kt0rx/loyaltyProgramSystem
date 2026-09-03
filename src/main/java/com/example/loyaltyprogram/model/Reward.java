package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateRewardRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "reward")
@Entity
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private LoyaltyProgram program;
    @Column(nullable = false)
    private String name;
    @Column(name = "points_cost", nullable = false)
    private int pointsCost;
    @Column(name = "available_quantity")
    private Integer availableQuantity;
    @Embedded
    private Period period;

    public Reward update(UpdateRewardRequest request) {
        this.name = request.name();
        this.pointsCost = request.pointsCost();
        this.availableQuantity = request.availableQuantity();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reward))
            return false;
        Reward other = (Reward) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
