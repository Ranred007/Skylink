package com.skylink.dto;

import com.skylink.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    private Priority priority = Priority.MEDIUM;

    // Constructors
    public ComplaintRequest() {}

    public ComplaintRequest(Long userId, String subject, String description, Priority priority) {
        this.userId = userId;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}