package com.skylink.dto;

import com.skylink.entity.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubscriptionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long planId;
    private String planName;
    private BigDecimal planPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public SubscriptionResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public BigDecimal getPlanPrice() { return planPrice; }
    public void setPlanPrice(BigDecimal planPrice) { this.planPrice = planPrice; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}