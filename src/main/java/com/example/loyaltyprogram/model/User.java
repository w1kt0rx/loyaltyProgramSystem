package com.example.loyaltyprogram.model;

import com.example.loyaltyprogram.dto.request.UpdateUserRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "app_user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(nullable = false)
    private boolean deactivated = false;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    public void addMembership(Membership membership) {
        memberships.add(membership);
        membership.setUser(this);
    }

    public void removeMembership(Membership membership) {
        memberships.remove(membership);
        membership.setUser(null);
    }

    public User update(UpdateUserRequest request) {
        this.firstName = request.firstName();
        this.lastName = request.lastName();
        return this;
    }

    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User))
            return false;
        User other = (User) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
