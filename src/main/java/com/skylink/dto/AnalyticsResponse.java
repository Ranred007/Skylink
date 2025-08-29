package com.skylink.dto;

public class AnalyticsResponse {
    // User Analytics
    private long totalUsers;
    private long totalCustomers;
    private long totalAdmins;

    // Subscription Analytics
    private long totalSubscriptions;
    private long activeSubscriptions;
    private long expiredSubscriptions;
    private long cancelledSubscriptions;
    private long subscriptionsExpiringNextWeek;

    // Complaint Analytics
    private long totalComplaints;
    private long openComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long closedComplaints;
    private long monthlyComplaints;

    // Constructors
    public AnalyticsResponse() {}

    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalAdmins() { return totalAdmins; }
    public void setTotalAdmins(long totalAdmins) { this.totalAdmins = totalAdmins; }

    public long getTotalSubscriptions() { return totalSubscriptions; }
    public void setTotalSubscriptions(long totalSubscriptions) { this.totalSubscriptions = totalSubscriptions; }

    public long getActiveSubscriptions() { return activeSubscriptions; }
    public void setActiveSubscriptions(long activeSubscriptions) { this.activeSubscriptions = activeSubscriptions; }

    public long getExpiredSubscriptions() { return expiredSubscriptions; }
    public void setExpiredSubscriptions(long expiredSubscriptions) { this.expiredSubscriptions = expiredSubscriptions; }

    public long getCancelledSubscriptions() { return cancelledSubscriptions; }
    public void setCancelledSubscriptions(long cancelledSubscriptions) { this.cancelledSubscriptions = cancelledSubscriptions; }

    public long getSubscriptionsExpiringNextWeek() { return subscriptionsExpiringNextWeek; }
    public void setSubscriptionsExpiringNextWeek(long subscriptionsExpiringNextWeek) { this.subscriptionsExpiringNextWeek = subscriptionsExpiringNextWeek; }

    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

    public long getOpenComplaints() { return openComplaints; }
    public void setOpenComplaints(long openComplaints) { this.openComplaints = openComplaints; }

    public long getInProgressComplaints() { return inProgressComplaints; }
    public void setInProgressComplaints(long inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; }

    public long getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    public long getClosedComplaints() { return closedComplaints; }
    public void setClosedComplaints(long closedComplaints) { this.closedComplaints = closedComplaints; }

    public long getMonthlyComplaints() { return monthlyComplaints; }
    public void setMonthlyComplaints(long monthlyComplaints) { this.monthlyComplaints = monthlyComplaints; }
}