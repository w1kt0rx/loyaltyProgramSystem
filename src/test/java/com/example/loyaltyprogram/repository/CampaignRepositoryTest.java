package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.Campaign;
import com.example.loyaltyprogram.model.EarningEventType;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Period;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class CampaignRepositoryTest {

    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private LoyaltyProgramRepository programRepository;

    @Test
    void globalCampaign_isFoundForAnyProgram() {
        LoyaltyProgram program = programRepository.save(newProgram());

        Campaign globalCampaign = new Campaign();
        globalCampaign.setProgram(null);
        globalCampaign.setName("Double points weekend");
        globalCampaign.setEventType(EarningEventType.PURCHASE);
        globalCampaign.setMultiplier(java.math.BigDecimal.valueOf(2));
        globalCampaign.setPeriod(new Period(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));
        campaignRepository.save(globalCampaign);

        List<Campaign> found = campaignRepository.findActiveCampaigns(
                EarningEventType.PURCHASE, program.getId(), LocalDateTime.now());

        assertThat(found).hasSize(1);
    }

    private LoyaltyProgram newProgram() {
        LoyaltyProgram p = new LoyaltyProgram();
        p.setName("Gold");
        p.setPeriod(new Period(LocalDateTime.now().minusDays(1), null));
        return p;
    }
}