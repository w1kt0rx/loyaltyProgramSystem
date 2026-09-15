package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.LoyaltyProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    boolean existsByName(String name);

    Optional<LoyaltyProgram> findByName(String name);

    @Query("""
            select p from LoyaltyProgram p
            where p.period.startDate <= :moment
              and (p.period.endDate is null or p.period.endDate >= :moment)
            """)
    Page<LoyaltyProgram> findActiveAt(@Param("moment") LocalDateTime moment, Pageable pageable);

    @Query("""
            select p from LoyaltyProgram p
            where p.period.endDate is null or p.period.endDate >= :moment
            """)
    Page<LoyaltyProgram> findNotExpired(@Param("moment") LocalDateTime moment, Pageable pageable);
}
