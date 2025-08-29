package com.skylink.dao;

import com.skylink.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    List<Plan> findByActiveTrue();
    
    List<Plan> findByActiveTrueOrderByPriceAsc();
    
    @Query("SELECT p FROM Plan p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Plan> findActivePlansByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                          @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT p FROM Plan p WHERE p.active = true AND p.dataLimitGB >= :minDataLimit")
    List<Plan> findActivePlansByMinDataLimit(@Param("minDataLimit") Integer minDataLimit);
    
    @Query("SELECT p FROM Plan p WHERE p.active = true AND p.speedMbps >= :minSpeed")
    List<Plan> findActivePlansByMinSpeed(@Param("minSpeed") Integer minSpeed);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId")
    long countSubscriptionsByPlanId(@Param("planId") Long planId);
}