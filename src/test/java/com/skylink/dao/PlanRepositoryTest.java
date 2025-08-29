package com.skylink.dao;

import com.skylink.entity.Plan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    private Plan plan1;
    private Plan plan2;

    @BeforeEach
    void setUp() {
        planRepository.deleteAll();

        plan1 = new Plan("Basic Plan", "Basic internet", new BigDecimal("299.99"), 30, 50, 20);
        plan2 = new Plan("Premium Plan", "High speed internet", new BigDecimal("999.99"), 90, 500, 100);

        planRepository.save(plan1);
        planRepository.save(plan2);
    }

    @Test
    void testFindByActiveTrue() {
        List<Plan> result = planRepository.findByActiveTrue();
        assertThat(result).hasSize(2);
    }

    @Test
    void testFindByActiveTrueOrderByPriceAsc() {
        List<Plan> result = planRepository.findByActiveTrueOrderByPriceAsc();
        assertThat(result).first().extracting(Plan::getName).isEqualTo("Basic Plan");
    }

    @Test
    void testFindActivePlansByPriceRange() {
        List<Plan> result = planRepository.findActivePlansByPriceRange(
                new BigDecimal("200.00"), new BigDecimal("500.00"));
        assertThat(result).contains(plan1).doesNotContain(plan2);
    }

    @Test
    void testFindActivePlansByMinDataLimit() {
        List<Plan> result = planRepository.findActivePlansByMinDataLimit(100);
        assertThat(result).contains(plan2).doesNotContain(plan1);
    }

    @Test
    void testFindActivePlansByMinSpeed() {
        List<Plan> result = planRepository.findActivePlansByMinSpeed(50);
        assertThat(result).contains(plan2).doesNotContain(plan1);
    }
}
