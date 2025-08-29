package com.skylink.service;

import com.skylink.dao.ComplaintRepository;
import com.skylink.dao.SubscriptionRepository;
import com.skylink.dao.UserRepository;
import com.skylink.dto.AnalyticsResponse;
import com.skylink.entity.ComplaintStatus;
import com.skylink.entity.Role;
import com.skylink.entity.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public AnalyticsResponse getAnalytics() {
        AnalyticsResponse analytics = new AnalyticsResponse();

        // User Analytics
        analytics.setTotalUsers(userRepository.count());
        analytics.setTotalCustomers(userRepository.countByRole(Role.CUSTOMER));
        analytics.setTotalAdmins(userRepository.countByRole(Role.ADMIN));

        // Subscription Analytics
        analytics.setTotalSubscriptions(subscriptionRepository.count());
        analytics.setActiveSubscriptions(subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE));
        analytics.setExpiredSubscriptions(subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED));
        analytics.setCancelledSubscriptions(subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED));

        // Complaint Analytics
        analytics.setTotalComplaints(complaintRepository.count());
        analytics.setOpenComplaints(complaintRepository.countByStatus(ComplaintStatus.OPEN));
        analytics.setInProgressComplaints(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS));
        analytics.setResolvedComplaints(complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        analytics.setClosedComplaints(complaintRepository.countByStatus(ComplaintStatus.CLOSED));

        // Monthly Analytics
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        
        analytics.setMonthlyComplaints(complaintRepository.findComplaintsBetweenDates(startOfMonth, endOfMonth).size());

        // Subscription expiring in next 7 days
        LocalDateTime nextWeek = LocalDateTime.now().plusDays(7);
        analytics.setSubscriptionsExpiringNextWeek(
            subscriptionRepository.findSubscriptionsExpiringBetween(LocalDateTime.now(), nextWeek).size()
        );

        return analytics;
    }

    public Map<String, Long> getUserGrowthAnalytics() {
        Map<String, Long> userGrowth = new HashMap<>();
        
        // This is a simplified version - in a real application, you'd want to group by date
        userGrowth.put("totalUsers", userRepository.count());
        userGrowth.put("customers", userRepository.countByRole(Role.CUSTOMER));
        userGrowth.put("admins", userRepository.countByRole(Role.ADMIN));
        
        return userGrowth;
    }

    public Map<String, Long> getSubscriptionAnalytics() {
        Map<String, Long> subscriptionStats = new HashMap<>();
        
        subscriptionStats.put("active", subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE));
        subscriptionStats.put("expired", subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED));
        subscriptionStats.put("cancelled", subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED));
        subscriptionStats.put("suspended", subscriptionRepository.countByStatus(SubscriptionStatus.SUSPENDED));
        
        return subscriptionStats;
    }

    public Map<String, Long> getComplaintAnalytics() {
        Map<String, Long> complaintStats = new HashMap<>();
        
        complaintStats.put("open", complaintRepository.countByStatus(ComplaintStatus.OPEN));
        complaintStats.put("inProgress", complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS));
        complaintStats.put("resolved", complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        complaintStats.put("closed", complaintRepository.countByStatus(ComplaintStatus.CLOSED));
        
        return complaintStats;
    }
}