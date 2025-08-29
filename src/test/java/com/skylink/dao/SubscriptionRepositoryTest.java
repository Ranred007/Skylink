package com.skylink.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.skylink.entity.Plan;
import com.skylink.entity.Role;
import com.skylink.entity.Subscription;
import com.skylink.entity.SubscriptionStatus;
import com.skylink.entity.User;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    private User testUser;
    private Plan testPlan;
    private Plan premiumPlan;
    private Subscription activeSubscription;

    @BeforeEach
    void setUp() {
        // Create and save user using the correct constructor
        testUser = new User("John Doe", "john@example.com", "1234567890", "password", Role.CUSTOMER);
        testUser = userRepository.save(testUser);

        // Create and save plans
        testPlan = new Plan(
            "Basic Plan", 
            "Basic internet plan", 
            BigDecimal.valueOf(100), 
            30,
            100,
            50
        );
        testPlan = planRepository.save(testPlan);

        premiumPlan = new Plan(
            "Premium Plan", 
            "Premium internet plan", 
            BigDecimal.valueOf(200), 
            30,
            500,
            100
        );
        premiumPlan = planRepository.save(premiumPlan);

        // Create active subscription
        activeSubscription = new Subscription(
            testUser,
            testPlan,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(30)
        );
        activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
        activeSubscription = subscriptionRepository.save(activeSubscription);
    }

    @Test
    void testFindByUser() {
        List<Subscription> subscriptions = subscriptionRepository.findByUser(testUser);
        assertThat(subscriptions).hasSize(1);
        assertThat(subscriptions.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindByUserOrderByCreatedAtDesc() {
        // Create another subscription for the same user
        Subscription secondSubscription = new Subscription(
            testUser,
            premiumPlan,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(15)
        );
        secondSubscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(secondSubscription);

        List<Subscription> subscriptions = subscriptionRepository.findByUserOrderByCreatedAtDesc(testUser);
        assertThat(subscriptions).hasSize(2);
        assertThat(subscriptions.get(0).getCreatedAt()).isAfterOrEqualTo(subscriptions.get(1).getCreatedAt());
    }

    @Test
    void testFindByStatus() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
        assertThat(activeSubscriptions).hasSize(1);
        assertThat(activeSubscriptions.get(0).getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testFindByUserAndStatus() {
        Optional<Subscription> subscription = subscriptionRepository.findByUserAndStatus(testUser, SubscriptionStatus.ACTIVE);
        assertThat(subscription).isPresent();
        assertThat(subscription.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(subscription.get().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testFindByUserAndStatus_NotFound() {
        Optional<Subscription> subscription = subscriptionRepository.findByUserAndStatus(testUser, SubscriptionStatus.CANCELLED);
        assertThat(subscription).isEmpty();
    }

    @Test
    void testFindActiveSubscriptionByUserId() {
        Optional<Subscription> subscription = subscriptionRepository.findActiveSubscriptionByUserId(testUser.getId());
        assertThat(subscription).isPresent();
        assertThat(subscription.get().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindActiveSubscriptionByUserId_NoActiveSubscription() {
        // Create user without active subscription
        User newUser = new User("Jane Doe", "jane@example.com", "0987654321", "password", Role.CUSTOMER);
        newUser = userRepository.save(newUser);

        Optional<Subscription> subscription = subscriptionRepository.findActiveSubscriptionByUserId(newUser.getId());
        assertThat(subscription).isEmpty();
    }

    @Test
    void testFindExpiredSubscriptions() {
        // Create expired subscription
        Subscription expiredSubscription = new Subscription(
            testUser,
            testPlan,
            LocalDateTime.now().minusDays(60),
            LocalDateTime.now().minusDays(30)
        );
        expiredSubscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(expiredSubscription);

        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
        assertThat(expiredSubscriptions).hasSize(1);
        assertThat(expiredSubscriptions.get(0).getEndDate()).isBefore(LocalDateTime.now());
    }

    @Test
    void testFindSubscriptionsExpiringBetween() {
        LocalDateTime startRange = LocalDateTime.now().plusDays(25);
        LocalDateTime endRange = LocalDateTime.now().plusDays(35);
        
        List<Subscription> expiringSubscriptions = subscriptionRepository.findSubscriptionsExpiringBetween(startRange, endRange);
        assertThat(expiringSubscriptions).hasSize(1);
        assertThat(expiringSubscriptions.get(0).getEndDate()).isBetween(startRange, endRange);
    }

    @Test
    void testCountByStatus() {
        long activeCount = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
        assertThat(activeCount).isEqualTo(1);

        long cancelledCount = subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED);
        assertThat(cancelledCount).isEqualTo(0);
    }

    @Test
    void testCountActiveSubscriptionsByPlanId() {
        long count = subscriptionRepository.countActiveSubscriptionsByPlanId(testPlan.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testCountActiveSubscriptionsByPlanId_MultipleSubscriptions() {
        // Create another user with same plan
        User secondUser = new User("Jane Smith", "jane.smith@example.com", "1111111111", "password", Role.CUSTOMER);
        secondUser = userRepository.save(secondUser);

        Subscription secondSubscription = new Subscription(
            secondUser,
            testPlan,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(30)
        );
        secondSubscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(secondSubscription);

        long count = subscriptionRepository.countActiveSubscriptionsByPlanId(testPlan.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testCountActiveSubscriptionsByPlanId_NoActiveSubscriptions() {
        long count = subscriptionRepository.countActiveSubscriptionsByPlanId(premiumPlan.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testFindByUser_NoSubscriptions() {
        User newUser = new User("New User", "new@example.com", "2222222222", "password", Role.CUSTOMER);
        newUser = userRepository.save(newUser);

        List<Subscription> subscriptions = subscriptionRepository.findByUser(newUser);
        assertThat(subscriptions).isEmpty();
    }

    @Test
    void testFindExpiredSubscriptions_NoneExpired() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now().minusDays(100));
        assertThat(expiredSubscriptions).isEmpty();
    }

    @Test
    void testFindSubscriptionsExpiringBetween_NoMatches() {
        LocalDateTime startRange = LocalDateTime.now().plusDays(100);
        LocalDateTime endRange = LocalDateTime.now().plusDays(200);
        
        List<Subscription> expiringSubscriptions = subscriptionRepository.findSubscriptionsExpiringBetween(startRange, endRange);
        assertThat(expiringSubscriptions).isEmpty();
    }

    @Test
    void testSubscriptionCreationTimestamps() {
        Subscription newSubscription = new Subscription(
            testUser,
            premiumPlan,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10)
        );
        newSubscription.setStatus(SubscriptionStatus.ACTIVE);
        
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);
        
        assertThat(savedSubscription.getCreatedAt()).isNotNull();
        assertThat(savedSubscription.getUpdatedAt()).isNotNull();
        assertThat(savedSubscription.getCreatedAt()).isEqualTo(savedSubscription.getUpdatedAt());
    }

    @Test
    void testSubscriptionUpdateTimestamps() {
        Subscription subscription = subscriptionRepository.findById(activeSubscription.getId()).get();
        LocalDateTime originalUpdatedAt = subscription.getUpdatedAt();
        
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        
        assertThat(updatedSubscription.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        assertThat(updatedSubscription.getCreatedAt()).isEqualTo(activeSubscription.getCreatedAt());
    }
}