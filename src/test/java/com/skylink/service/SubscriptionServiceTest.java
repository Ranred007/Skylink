package com.skylink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skylink.dao.PlanRepository;
import com.skylink.dao.SubscriptionRepository;
import com.skylink.dao.UserRepository;
import com.skylink.dto.SubscriptionRequest;
import com.skylink.dto.SubscriptionResponse;
import com.skylink.entity.Plan;
import com.skylink.entity.Subscription;
import com.skylink.entity.SubscriptionStatus;
import com.skylink.entity.User;
import com.skylink.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private Plan testPlan;
    private Subscription activeSubscription;
    private Subscription expiredSubscription;
    private SubscriptionRequest subscriptionRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User("John Doe", "john@example.com", "1234567890", "password", null);
        testUser.setId(1L);

        // Create test plan
        testPlan = new Plan();
        testPlan.setId(1L);
        testPlan.setName("Basic Plan");
        testPlan.setPrice(BigDecimal.valueOf(100));
        testPlan.setDurationInDays(30);
        testPlan.setDataLimitGB(100);
        testPlan.setSpeedMbps(50);
        testPlan.setActive(true);

        // Create active subscription
        LocalDateTime now = LocalDateTime.now();
        activeSubscription = new Subscription();
        activeSubscription.setId(1L);
        activeSubscription.setUser(testUser);
        activeSubscription.setPlan(testPlan);
        activeSubscription.setStartDate(now.minusDays(10));
        activeSubscription.setEndDate(now.plusDays(20));
        activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
        activeSubscription.setCreatedAt(now.minusDays(10));
        activeSubscription.setUpdatedAt(now.minusDays(10));

        // Create expired subscription
        expiredSubscription = new Subscription();
        expiredSubscription.setId(2L);
        expiredSubscription.setUser(testUser);
        expiredSubscription.setPlan(testPlan);
        expiredSubscription.setStartDate(now.minusDays(40));
        expiredSubscription.setEndDate(now.minusDays(10));
        expiredSubscription.setStatus(SubscriptionStatus.EXPIRED);
        expiredSubscription.setCreatedAt(now.minusDays(40));
        expiredSubscription.setUpdatedAt(now.minusDays(10));

        // Create subscription request
        subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setUserId(1L);
        subscriptionRequest.setPlanId(1L);
    }

    @Test
    void testCreateSubscription_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(planRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.findActiveSubscriptionByUserId(1L)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> {
            Subscription sub = invocation.getArgument(0);
            sub.setId(3L);
            return sub;
        });

        SubscriptionResponse response = subscriptionService.createSubscription(subscriptionRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getPlanId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(response.getStartDate()).isNotNull();
        assertThat(response.getEndDate()).isAfter(response.getStartDate());

        verify(userRepository).findById(1L);
        verify(planRepository).findById(1L);
        verify(subscriptionRepository).findActiveSubscriptionByUserId(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void testCreateSubscription_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.createSubscription(subscriptionRequest);
        });

        verify(userRepository).findById(1L);
        verify(planRepository, never()).findById(any());
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testCreateSubscription_PlanNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.createSubscription(subscriptionRequest);
        });

        verify(userRepository).findById(1L);
        verify(planRepository).findById(1L);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testCreateSubscription_UserHasActiveSubscription() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(planRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.findActiveSubscriptionByUserId(1L)).thenReturn(Optional.of(activeSubscription));

        assertThrows(IllegalStateException.class, () -> {
            subscriptionService.createSubscription(subscriptionRequest);
        });

        verify(userRepository).findById(1L);
        verify(planRepository).findById(1L);
        verify(subscriptionRepository).findActiveSubscriptionByUserId(1L);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testGetUserSubscriptions() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUserOrderByCreatedAtDesc(testUser))
            .thenReturn(List.of(activeSubscription, expiredSubscription));

        List<SubscriptionResponse> responses = subscriptionService.getUserSubscriptions(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(SubscriptionResponse::getUserId).containsOnly(1L);
        verify(userRepository).findById(1L);
        verify(subscriptionRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    void testGetUserSubscriptions_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.getUserSubscriptions(1L);
        });

        verify(userRepository).findById(1L);
        verify(subscriptionRepository, never()).findByUserOrderByCreatedAtDesc(any());
    }

    @Test
    void testGetActiveSubscription_Success() {
        when(subscriptionRepository.findActiveSubscriptionByUserId(1L)).thenReturn(Optional.of(activeSubscription));

        SubscriptionResponse response = subscriptionService.getActiveSubscription(1L);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(subscriptionRepository).findActiveSubscriptionByUserId(1L);
    }

    @Test
    void testGetActiveSubscription_NotFound() {
        when(subscriptionRepository.findActiveSubscriptionByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.getActiveSubscription(1L);
        });

        verify(subscriptionRepository).findActiveSubscriptionByUserId(1L);
    }

    @Test
    void testGetAllSubscriptions() {
        when(subscriptionRepository.findAll()).thenReturn(List.of(activeSubscription, expiredSubscription));

        List<SubscriptionResponse> responses = subscriptionService.getAllSubscriptions();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(SubscriptionResponse::getId).contains(1L, 2L);
        verify(subscriptionRepository).findAll();
    }

    @Test
    void testGetSubscriptionsByStatus() {
        when(subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE))
            .thenReturn(List.of(activeSubscription));

        List<SubscriptionResponse> responses = subscriptionService.getSubscriptionsByStatus(SubscriptionStatus.ACTIVE);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        verify(subscriptionRepository).findByStatus(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testUpdateSubscriptionStatus_Success() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(activeSubscription);

        SubscriptionResponse response = subscriptionService.updateSubscriptionStatus(1L, SubscriptionStatus.CANCELLED);

        assertThat(response.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void testUpdateSubscriptionStatus_NotFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            subscriptionService.updateSubscriptionStatus(1L, SubscriptionStatus.CANCELLED);
        });

        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testCancelSubscription() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        subscriptionService.cancelSubscription(1L);

        assertThat(activeSubscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void testRenewSubscription() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(expiredSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        subscriptionService.renewSubscription(1L);

        assertThat(expiredSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(expiredSubscription.getStartDate()).isAfterOrEqualTo(LocalDateTime.now().minusMinutes(1));
        assertThat(expiredSubscription.getEndDate()).isAfter(expiredSubscription.getStartDate());
        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

//    @Test
//    void testProcessExpiredSubscriptions() {
//        LocalDateTime now = LocalDateTime.now();
//        when(subscriptionRepository.findExpiredSubscriptions(now)).thenReturn(List.of(expiredSubscription));
//        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        subscriptionService.processExpiredSubscriptions();
//
//        // The expired subscription should remain expired (no change)
//        assertThat(expiredSubscription.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
//        verify(subscriptionRepository).findExpiredSubscriptions(now);
//        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
//    }

//    @Test
//    void testProcessExpiredSubscriptions_WithActiveSubscriptions() {
//        LocalDateTime now = LocalDateTime.now();
//        // Create an active subscription that should be expired
//        Subscription soonToExpire = new Subscription();
//        soonToExpire.setId(3L);
//        soonToExpire.setUser(testUser);
//        soonToExpire.setPlan(testPlan);
//        soonToExpire.setStartDate(now.minusDays(35));
//        soonToExpire.setEndDate(now.minusDays(1)); // Ended yesterday
//        soonToExpire.setStatus(SubscriptionStatus.ACTIVE);
//
//        when(subscriptionRepository.findExpiredSubscriptions(now)).thenReturn(List.of(soonToExpire));
//        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        subscriptionService.processExpiredSubscriptions();
//
//        assertThat(soonToExpire.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
//        verify(subscriptionRepository).findExpiredSubscriptions(now);
//        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
//    }

    @Test
    void testGetTotalActiveSubscriptions() {
        when(subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE)).thenReturn(5L);

        long count = subscriptionService.getTotalActiveSubscriptions();

        assertThat(count).isEqualTo(5L);
        verify(subscriptionRepository).countByStatus(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testGetTotalExpiredSubscriptions() {
        when(subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED)).thenReturn(3L);

        long count = subscriptionService.getTotalExpiredSubscriptions();

        assertThat(count).isEqualTo(3L);
        verify(subscriptionRepository).countByStatus(SubscriptionStatus.EXPIRED);
    }

    @Test
    void testConvertToSubscriptionResponse() {
        SubscriptionResponse response = subscriptionService.convertToSubscriptionResponse(activeSubscription);

        assertThat(response.getId()).isEqualTo(activeSubscription.getId());
        assertThat(response.getUserId()).isEqualTo(activeSubscription.getUser().getId());
        assertThat(response.getPlanId()).isEqualTo(activeSubscription.getPlan().getId());
        assertThat(response.getPlanName()).isEqualTo(activeSubscription.getPlan().getName());
        assertThat(response.getPlanPrice()).isEqualTo(activeSubscription.getPlan().getPrice());
        assertThat(response.getStartDate()).isEqualTo(activeSubscription.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(activeSubscription.getEndDate());
        assertThat(response.getStatus()).isEqualTo(activeSubscription.getStatus());
        assertThat(response.getCreatedAt()).isEqualTo(activeSubscription.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(activeSubscription.getUpdatedAt());
    }

    @Test
    void testGetUserSubscriptions_EmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(List.of());

        List<SubscriptionResponse> responses = subscriptionService.getUserSubscriptions(1L);

        assertThat(responses).isEmpty();
        verify(userRepository).findById(1L);
        verify(subscriptionRepository).findByUserOrderByCreatedAtDesc(testUser);
    }
}