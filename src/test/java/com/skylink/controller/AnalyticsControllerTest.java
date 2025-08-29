package com.skylink.controller;

import com.skylink.dto.AnalyticsResponse;
import com.skylink.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    private AnalyticsResponse analyticsResponse;
    private Map<String, Long> dummyStats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        analyticsResponse = new AnalyticsResponse();
        analyticsResponse.setTotalUsers(100L);
        analyticsResponse.setActiveSubscriptions(75L);
        analyticsResponse.setResolvedComplaints(50L);

        dummyStats = new HashMap<>();
        dummyStats.put("2025-01", 10L);
        dummyStats.put("2025-02", 20L);
    }

    @Test
    void testGetAnalytics() {
        when(analyticsService.getAnalytics()).thenReturn(analyticsResponse);

        ResponseEntity<AnalyticsResponse> response = analyticsController.getAnalytics();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(100L, response.getBody().getTotalUsers());
        assertEquals(75L, response.getBody().getActiveSubscriptions());
        assertEquals(50L, response.getBody().getResolvedComplaints());
    }

    @Test
    void testGetUserGrowthAnalytics() {
        when(analyticsService.getUserGrowthAnalytics()).thenReturn(dummyStats);

        ResponseEntity<Map<String, Long>> response = analyticsController.getUserGrowthAnalytics();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(10L, response.getBody().get("2025-01"));
    }

    @Test
    void testGetSubscriptionAnalytics() {
        when(analyticsService.getSubscriptionAnalytics()).thenReturn(dummyStats);

        ResponseEntity<Map<String, Long>> response = analyticsController.getSubscriptionAnalytics();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(20L, response.getBody().get("2025-02"));
    }

    @Test
    void testGetComplaintAnalytics() {
        when(analyticsService.getComplaintAnalytics()).thenReturn(dummyStats);

        ResponseEntity<Map<String, Long>> response = analyticsController.getComplaintAnalytics();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("2025-01"));
    }
}
