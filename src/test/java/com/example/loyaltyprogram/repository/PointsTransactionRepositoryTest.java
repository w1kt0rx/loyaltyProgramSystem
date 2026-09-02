package com.example.loyaltyprogram.repository;

import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.Period;
import com.example.loyaltyprogram.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class PointsTransactionRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoyaltyProgramRepository programRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private PointsTransactionRepository transactionRepository;

    @Test
    void findGlobalRanking_sortsInDatabaseDescending() {
        LoyaltyProgram program = programRepository.save(program("Silver"));

        Membership low = membershipRepository.save(membership(user("a@a.pl", "Ala"), program, 10));
        Membership high = membershipRepository.save(membership(user("b@b.pl", "Bea"), program, 100));

        List<RankingEntry> ranking = transactionRepository.findGlobalRanking(PageRequest.of(0, 10));

        assertThat(ranking).hasSize(2);
        assertThat(ranking.get(0).getTotalPoints()).isEqualTo(100L);
        assertThat(ranking.get(1).getTotalPoints()).isEqualTo(10L);
    }

    private User user(String email, String firstName) {
        User u = new User();
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName("Test");
        return userRepository.save(u);
    }

    private LoyaltyProgram program(String name) {
        LoyaltyProgram p = new LoyaltyProgram();
        p.setName(name);
        p.setPeriod(new Period(LocalDateTime.now().minusDays(1), null));
        return p;
    }

    private Membership membership(User user, LoyaltyProgram program, int balance) {
        Membership m = new Membership();
        m.setUser(user);
        m.setProgram(program);
        m.setJoinDate(LocalDate.now());
        m.setPointsBalance(balance);
        return m;
    }
}