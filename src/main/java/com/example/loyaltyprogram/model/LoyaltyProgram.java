package com.example.loyaltyprogram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "loyalty_program")
@Entity
public class LoyaltyProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @Embedded
    private Period period;
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EarningRule> earningRules = new ArrayList<>();
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reward> rewards = new ArrayList<>();

    public void addMembership(Membership membership) {
        memberships.add(membership);
        membership.setProgram(this);
    }

    public void addEarningRule(EarningRule earningRule) {
        earningRules.add(earningRule);
        earningRule.setProgram(this);
    }

    public void removeEarningRule(EarningRule earningRule) {
        earningRules.remove(earningRule);
        earningRule.setProgram(null);
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
        reward.setProgram(this);
    }

    public void removeReward(Reward reward) {
        earningRules.remove(reward);
        reward.setProgram(null);
    }

    public boolean isActiveAt(LocalDateTime moment) {
        return period.isActiveAt(moment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoyaltyProgram))
            return false;
        LoyaltyProgram other = (LoyaltyProgram) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
