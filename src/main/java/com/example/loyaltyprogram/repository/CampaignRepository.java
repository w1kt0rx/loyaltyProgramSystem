package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.Campaign;
import com.example.loyaltyprogram.model.EarningEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    @Query("""
        select c from Campaign c
        where c.eventType = :eventType
        and (c.program.id = :programId or c.program is null)
        and c.period.startDate <= :moment
        and (c.period.endDate is null or c.period.endDate >= :moment)
        """)
    List<Campaign> findActiveCampaigns(
            @Param("eventType") EarningEventType eventType,
            @Param("programId") Long programId,
            @Param("moment") LocalDateTime moment
    );
}
