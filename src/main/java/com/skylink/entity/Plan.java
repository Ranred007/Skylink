package com.skylink.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plan name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(nullable = false)
    private Integer durationInDays;

    @Column(name = "data_limit_gb")
    private Integer dataLimitGB;

    @Column(name = "speed_mbps")
    private Integer speedMbps;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Plan() {}

    public Plan(String name, String description, BigDecimal price, Integer durationInDays, 
                Integer dataLimitGB, Integer speedMbps) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationInDays = durationInDays;
        this.dataLimitGB = dataLimitGB;
        this.speedMbps = speedMbps;
    }

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

    public List<Subscription> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<Subscription> subscriptions) { this.subscriptions = subscriptions; }
}