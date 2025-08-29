package com.skylink.dto;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Plan ID is required")
    private Long planId;

    // Constructors
    public SubscriptionRequest() {}

    public SubscriptionRequest(Long userId, Long planId) {
        this.userId = userId;
        this.planId = planId;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
}