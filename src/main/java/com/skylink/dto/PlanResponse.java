package com.skylink.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlanResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationInDays;
    private Integer dataLimitGB;
    private Integer speedMbps;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PlanResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getDurationInDays() { return durationInDays; }
    public void setDurationInDays(Integer durationInDays) { this.durationInDays = durationInDays; }

    public Integer getDataLimitGB() { return dataLimitGB; }
    public void setDataLimitGB(Integer dataLimitGB) { this.dataLimitGB = dataLimitGB; }

    public Integer getSpeedMbps() { return speedMbps; }
    public void setSpeedMbps(Integer speedMbps) { this.speedMbps = speedMbps; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}