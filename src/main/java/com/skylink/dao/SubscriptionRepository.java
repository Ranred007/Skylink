//package com.skylink.dao;
//
//import com.skylink.entity.Subscription;
//import com.skylink.entity.SubscriptionStatus;
//import com.skylink.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
//    
//    List<Subscription> findByUser(User user);
//    
//    List<Subscription> findByUserOrderByCreatedAtDesc(User user);
//    
//    List<Subscription> findByStatus(SubscriptionStatus status);
//    
//    Optional<Subscription> findByUserAndStatus(User user, SubscriptionStatus status);
//    
//    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
//    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") Long userId);
//    
//    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.status = 'ACTIVE'")
//    List<Subscription> findExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);
//    
//    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :startDate AND :endDate")
//    List<Subscription> findSubscriptionsExpiringBetween(@Param("startDate") LocalDateTime startDate, 
//                                                        @Param("endDate") LocalDateTime endDate);
//    
//    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status")
//    long countByStatus(@Param("status") SubscriptionStatus status);
//    
//    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.status = 'ACTIVE'")
//    long countActiveSubscriptionsByPlanId(@Param("planId") Long planId);
//}

package com.skylink.dao;

import com.skylink.entity.Subscription;
import com.skylink.entity.SubscriptionStatus;
import com.skylink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser(User user);

    List<Subscription> findByUserOrderByCreatedAtDesc(User user);

    List<Subscription> findByStatus(SubscriptionStatus status);

    Optional<Subscription> findByUserAndStatus(User user, SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = com.skylink.entity.SubscriptionStatus.ACTIVE")
    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.status = com.skylink.entity.SubscriptionStatus.ACTIVE")
    List<Subscription> findExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsExpiringBetween(@Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status")
    long countByStatus(@Param("status") SubscriptionStatus status);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.status = com.skylink.entity.SubscriptionStatus.ACTIVE")
    long countActiveSubscriptionsByPlanId(@Param("planId") Long planId);
}
