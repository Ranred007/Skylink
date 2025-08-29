package com.skylink.controller;

import com.skylink.dto.SubscriptionRequest;
import com.skylink.dto.SubscriptionResponse;
import com.skylink.entity.SubscriptionStatus;
import com.skylink.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody SubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionService.createSubscription(request);
        return new ResponseEntity<>(subscription, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<SubscriptionResponse>> getUserSubscriptions(@PathVariable Long userId) {
        List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(@PathVariable Long userId) {
        SubscriptionResponse subscription = subscriptionService.getActiveSubscription(userId);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
        List<SubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByStatus(@PathVariable SubscriptionStatus status) {
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByStatus(status);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponse> updateSubscriptionStatus(@PathVariable Long id, 
                                                                        @RequestParam SubscriptionStatus status) {
        SubscriptionResponse subscription = subscriptionService.updateSubscriptionStatus(id, status);
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionService.getSubscriptionById(#id).userId == authentication.principal.id")
    public ResponseEntity<String> cancelSubscription(@PathVariable Long id) {
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.ok("Subscription cancelled successfully");
    }

    @PutMapping("/{id}/renew")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionService.getSubscriptionById(#id).userId == authentication.principal.id")
    public ResponseEntity<String> renewSubscription(@PathVariable Long id) {
        subscriptionService.renewSubscription(id);
        return ResponseEntity.ok("Subscription renewed successfully");
    }

    @GetMapping("/stats/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalActiveSubscriptions() {
        long count = subscriptionService.getTotalActiveSubscriptions();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalExpiredSubscriptions() {
        long count = subscriptionService.getTotalExpiredSubscriptions();
        return ResponseEntity.ok(count);
    }
}