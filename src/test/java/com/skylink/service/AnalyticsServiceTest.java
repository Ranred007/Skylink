package com.skylink.service;

import com.skylink.dao.ComplaintRepository;
import com.skylink.dao.SubscriptionRepository;
import com.skylink.dao.UserRepository;
import com.skylink.dto.AnalyticsResponse;
import com.skylink.entity.Complaint;
import com.skylink.entity.ComplaintStatus;
import com.skylink.entity.Role;
import com.skylink.entity.Subscription;
import com.skylink.entity.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAnalytics() {
        // Mock User counts
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(7L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(3L);

        // Mock Subscription counts
        when(subscriptionRepository.count()).thenReturn(5L);
        when(subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE)).thenReturn(3L);
        when(subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED)).thenReturn(1L);
        when(subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED)).thenReturn(1L);

        // Mock Complaint counts
        when(complaintRepository.count()).thenReturn(4L);
        when(complaintRepository.countByStatus(ComplaintStatus.OPEN)).thenReturn(2L);
        when(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS)).thenReturn(1L);
        when(complaintRepository.countByStatus(ComplaintStatus.RESOLVED)).thenReturn(1L);
        when(complaintRepository.countByStatus(ComplaintStatus.CLOSED)).thenReturn(0L);

     // Mock monthly complaints
        Complaint mockComplaint = new Complaint();
        when(complaintRepository.findComplaintsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockComplaint));

        // Mock subscriptions expiring next week
        Subscription mockSubscription = new Subscription();
        when(subscriptionRepository.findSubscriptionsExpiringBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockSubscription));


        AnalyticsResponse response = analyticsService.getAnalytics();

        // Verify response values
        assertEquals(10L, response.getTotalUsers());
        assertEquals(7L, response.getTotalCustomers());
        assertEquals(3L, response.getTotalAdmins());

        assertEquals(5L, response.getTotalSubscriptions());
        assertEquals(3L, response.getActiveSubscriptions());
        assertEquals(1L, response.getExpiredSubscriptions());
        assertEquals(1L, response.getCancelledSubscriptions());

        assertEquals(4L, response.getTotalComplaints());
        assertEquals(2L, response.getOpenComplaints());
        assertEquals(1L, response.getInProgressComplaints());
        assertEquals(1L, response.getResolvedComplaints());
        assertEquals(0L, response.getClosedComplaints());

        assertEquals(1, response.getMonthlyComplaints());
        assertEquals(1, response.getSubscriptionsExpiringNextWeek());

        // Verify repository calls
        verify(userRepository, times(1)).count();
        verify(subscriptionRepository, times(1)).count();
        verify(complaintRepository, times(1)).count();
    }

    @Test
    void testGetUserGrowthAnalytics() {
        when(userRepository.count()).thenReturn(20L);
        when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(15L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);

        Map<String, Long> result = analyticsService.getUserGrowthAnalytics();

        assertEquals(20L, result.get("totalUsers"));
        assertEquals(15L, result.get("customers"));
        assertEquals(5L, result.get("admins"));
    }

    @Test
    void testGetSubscriptionAnalytics() {
        when(subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE)).thenReturn(4L);
        when(subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED)).thenReturn(2L);
        when(subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED)).thenReturn(1L);
        when(subscriptionRepository.countByStatus(SubscriptionStatus.SUSPENDED)).thenReturn(0L);

        Map<String, Long> result = analyticsService.getSubscriptionAnalytics();

        assertEquals(4L, result.get("active"));
        assertEquals(2L, result.get("expired"));
        assertEquals(1L, result.get("cancelled"));
        assertEquals(0L, result.get("suspended"));
    }

    @Test
    void testGetComplaintAnalytics() {
        when(complaintRepository.countByStatus(ComplaintStatus.OPEN)).thenReturn(5L);
        when(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS)).thenReturn(3L);
        when(complaintRepository.countByStatus(ComplaintStatus.RESOLVED)).thenReturn(2L);
        when(complaintRepository.countByStatus(ComplaintStatus.CLOSED)).thenReturn(1L);

        Map<String, Long> result = analyticsService.getComplaintAnalytics();

        assertEquals(5L, result.get("open"));
        assertEquals(3L, result.get("inProgress"));
        assertEquals(2L, result.get("resolved"));
        assertEquals(1L, result.get("closed"));
    }
}
