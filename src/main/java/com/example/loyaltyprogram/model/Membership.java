package com.example.loyaltyprogram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Table(name = "membership")
@Entity
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private LoyaltyProgram program;
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;
    @Column(name = "points_balance", nullable = false)
    private int pointsBalance = 0;
    @OneToMany(mappedBy = "membership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointsTransaction> transactions = new ArrayList<>();

    public Membership() {
        this.joinDate = LocalDate.now();
    }

    public void addTransaction(PointsTransaction transaction) {
        transactions.add(transaction);
        transaction.setMembership(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membership))
            return false;
        Membership other = (Membership) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
