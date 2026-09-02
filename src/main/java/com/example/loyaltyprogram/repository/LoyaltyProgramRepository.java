package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    boolean existsByName(String name);

    Optional<LoyaltyProgram> findByName(String name);
}
