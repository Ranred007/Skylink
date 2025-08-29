package com.skylink.controller;

import com.skylink.dto.SubscriptionRequest;
import com.skylink.dto.SubscriptionResponse;
import com.skylink.entity.SubscriptionStatus;
import com.skylink.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    private SubscriptionResponse subscriptionResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        subscriptionResponse = new SubscriptionResponse();
        subscriptionResponse.setId(1L);
        subscriptionResponse.setUserId(100L);
        subscriptionResponse.setPlanId(200L);
        subscriptionResponse.setStatus(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testCreateSubscription() {
        SubscriptionRequest request = new SubscriptionRequest();
        when(subscriptionService.createSubscription(request)).thenReturn(subscriptionResponse);

        ResponseEntity<SubscriptionResponse> response = subscriptionController.createSubscription(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(subscriptionService, times(1)).createSubscription(request);
    }

    @Test
    void testGetUserSubscriptions() {
        when(subscriptionService.getUserSubscriptions(100L)).thenReturn(Arrays.asList(subscriptionResponse));

        ResponseEntity<List<SubscriptionResponse>> response = subscriptionController.getUserSubscriptions(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(subscriptionService, times(1)).getUserSubscriptions(100L);
    }

    @Test
    void testGetActiveSubscription() {
        when(subscriptionService.getActiveSubscription(100L)).thenReturn(subscriptionResponse);

        ResponseEntity<SubscriptionResponse> response = subscriptionController.getActiveSubscription(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(SubscriptionStatus.ACTIVE, response.getBody().getStatus());
        verify(subscriptionService, times(1)).getActiveSubscription(100L);
    }

    @Test
    void testGetAllSubscriptions() {
        when(subscriptionService.getAllSubscriptions()).thenReturn(Arrays.asList(subscriptionResponse));

        ResponseEntity<List<SubscriptionResponse>> response = subscriptionController.getAllSubscriptions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(subscriptionService, times(1)).getAllSubscriptions();
    }

    @Test
    void testGetSubscriptionsByStatus() {
        when(subscriptionService.getSubscriptionsByStatus(SubscriptionStatus.ACTIVE))
                .thenReturn(Arrays.asList(subscriptionResponse));

        ResponseEntity<List<SubscriptionResponse>> response =
                subscriptionController.getSubscriptionsByStatus(SubscriptionStatus.ACTIVE);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(SubscriptionStatus.ACTIVE, response.getBody().get(0).getStatus());
        verify(subscriptionService, times(1)).getSubscriptionsByStatus(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testUpdateSubscriptionStatus() {
        when(subscriptionService.updateSubscriptionStatus(1L, SubscriptionStatus.CANCELLED))
                .thenReturn(subscriptionResponse);

        ResponseEntity<SubscriptionResponse> response =
                subscriptionController.updateSubscriptionStatus(1L, SubscriptionStatus.CANCELLED);

        assertEquals(200, response.getStatusCodeValue());
        verify(subscriptionService, times(1)).updateSubscriptionStatus(1L, SubscriptionStatus.CANCELLED);
    }

    @Test
    void testCancelSubscription() {
        doNothing().when(subscriptionService).cancelSubscription(1L);

        ResponseEntity<String> response = subscriptionController.cancelSubscription(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Subscription cancelled successfully", response.getBody());
        verify(subscriptionService, times(1)).cancelSubscription(1L);
    }

    @Test
    void testRenewSubscription() {
        doNothing().when(subscriptionService).renewSubscription(1L);

        ResponseEntity<String> response = subscriptionController.renewSubscription(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Subscription renewed successfully", response.getBody());
        verify(subscriptionService, times(1)).renewSubscription(1L);
    }

    @Test
    void testGetTotalActiveSubscriptions() {
        when(subscriptionService.getTotalActiveSubscriptions()).thenReturn(5L);

        ResponseEntity<Long> response = subscriptionController.getTotalActiveSubscriptions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5L, response.getBody());
        verify(subscriptionService, times(1)).getTotalActiveSubscriptions();
    }

    @Test
    void testGetTotalExpiredSubscriptions() {
        when(subscriptionService.getTotalExpiredSubscriptions()).thenReturn(2L);

        ResponseEntity<Long> response = subscriptionController.getTotalExpiredSubscriptions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2L, response.getBody());
        verify(subscriptionService, times(1)).getTotalExpiredSubscriptions();
    }
}
