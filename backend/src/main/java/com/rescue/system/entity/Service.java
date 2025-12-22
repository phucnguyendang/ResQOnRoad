package com.rescue.system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "services", indexes = {
        @Index(name = "idx_service_type", columnList = "type")
})
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ServiceType type;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "price_unit", length = 50)
    private String priceUnit = "VND"; // VND, USD, per km, per hour

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "estimated_time")
    private Integer estimatedTime; // Thời gian ước tính (phút)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private RescueCompany company;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Service() {
    }

    public Service(Long id, String name, String description, ServiceType type, Double basePrice, String priceUnit,
            Boolean isAvailable, Integer estimatedTime, RescueCompany company, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.basePrice = basePrice;
        this.priceUnit = priceUnit;
        this.isAvailable = isAvailable;
        this.estimatedTime = estimatedTime;
        this.company = company;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public RescueCompany getCompany() {
        return company;
    }

    public void setCompany(RescueCompany company) {
        this.company = company;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", basePrice=" + basePrice +
                ", priceUnit='" + priceUnit + '\'' +
                ", isAvailable=" + isAvailable +
                ", estimatedTime=" + estimatedTime +
                ", createdAt=" + createdAt +
                '}';
    }
}
