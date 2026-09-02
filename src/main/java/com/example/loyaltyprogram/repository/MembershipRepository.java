package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    boolean existsByUserIdAndProgramId(Long userId, Long programId);

    Optional<Membership> findByUserIdAndProgramId(Long userId, Long programId);

    List<Membership> findByUserId(Long userId);

    List<Membership> findByProgramId(Long programId);
}
