package com.skylink.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));

        // Check if user already has an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findActiveSubscriptionByUserId(user.getId());
        if (existingSubscription.isPresent()) {
            throw new IllegalStateException("User already has an active subscription");
        }

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(plan.getDurationInDays());

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToSubscriptionResponse(savedSubscription);
    }

    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return subscriptionRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse getActiveSubscription(Long userId) {
        Optional<Subscription> subscription = subscriptionRepository.findActiveSubscriptionByUserId(userId);
        if (subscription.isPresent()) {
            return convertToSubscriptionResponse(subscription.get());
        }
        throw new ResourceNotFoundException("No active subscription found for user id: " + userId);
    }

    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(this::convertToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public List<SubscriptionResponse> getSubscriptionsByStatus(SubscriptionStatus status) {
        return subscriptionRepository.findByStatus(status).stream()
                .map(this::convertToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse updateSubscriptionStatus(Long id, SubscriptionStatus status) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setStatus(status);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToSubscriptionResponse(updatedSubscription);
    }

    public void cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    public void renewSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        LocalDateTime newStartDate = LocalDateTime.now();
        LocalDateTime newEndDate = newStartDate.plusDays(subscription.getPlan().getDurationInDays());

        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(subscription);
    }

    public void processExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
        }
    }

    public long getTotalActiveSubscriptions() {
        return subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
    }

    public long getTotalExpiredSubscriptions() {
        return subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED);
    }

    SubscriptionResponse convertToSubscriptionResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setUserId(subscription.getUser().getId());
        response.setUserName(subscription.getUser().getName());
        response.setUserEmail(subscription.getUser().getEmail());
        response.setPlanId(subscription.getPlan().getId());
        response.setPlanName(subscription.getPlan().getName());
        response.setPlanPrice(subscription.getPlan().getPrice());
        response.setStartDate(subscription.getStartDate());
        response.setEndDate(subscription.getEndDate());
        response.setStatus(subscription.getStatus());
        response.setCreatedAt(subscription.getCreatedAt());
        response.setUpdatedAt(subscription.getUpdatedAt());
        return response;
    }
}