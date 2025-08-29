package com.skylink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PlanRequest {
    @NotBlank(message = "Plan name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationInDays;

    private Integer dataLimitGB;
    private Integer speedMbps;

    // Constructors
    public PlanRequest() {}

    public PlanRequest(String name, String description, BigDecimal price, Integer durationInDays, 
                      Integer dataLimitGB, Integer speedMbps) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationInDays = durationInDays;
        this.dataLimitGB = dataLimitGB;
        this.speedMbps = speedMbps;
    }

    // Getters and Setters
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
}