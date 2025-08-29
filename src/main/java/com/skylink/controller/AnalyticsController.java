package com.skylink.controller;

import com.skylink.dto.AnalyticsResponse;
import com.skylink.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        AnalyticsResponse analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Long>> getUserGrowthAnalytics() {
        Map<String, Long> userGrowth = analyticsService.getUserGrowthAnalytics();
        return ResponseEntity.ok(userGrowth);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<Map<String, Long>> getSubscriptionAnalytics() {
        Map<String, Long> subscriptionStats = analyticsService.getSubscriptionAnalytics();
        return ResponseEntity.ok(subscriptionStats);
    }

    @GetMapping("/complaints")
    public ResponseEntity<Map<String, Long>> getComplaintAnalytics() {
        Map<String, Long> complaintStats = analyticsService.getComplaintAnalytics();
        return ResponseEntity.ok(complaintStats);
    }
}