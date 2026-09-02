package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.PointsTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {
    boolean existsByMembershipIdAndReferenceId(Long membershipId, Long referenceId);

    Optional<PointsTransaction> findByMembershipIdAndReferenceId(Long membershipId, Long referenceId);

    Page<PointsTransaction> findByMembershipIdOrderByOccurredAtDesc(
            Long membershipId, Pageable pageable
    );

    @Query("""
            select m.user.id as userId,
                   concat(m.user.firstName, ' ', m.user.lastName) as displayName,
                   sum(m.pointsBalance) as totalPoints
            from Membership m
            group by m.user.id, m.user.firstName, m.user.lastName
            order by sum(m.pointsBalance) desc
            """)
    List<RankingEntry> findGlobalRanking(Pageable pageable);

    @Query("""
            select m.user.id as userId,
                   concat(m.user.firstName, ' ', m.user.lastName) as displayName,
                   m.pointsBalance as totalPoints
            from Membership m
            where m.program.id = :programId
            order by m.pointsBalance desc
            """)
    List<RankingEntry> findRankingByProgram(@Param("programId") Long programId, Pageable pageable);
}
