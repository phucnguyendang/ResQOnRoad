package com.rescue.system.dto.response;

import java.time.LocalDateTime;

public class ServiceDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String typeDisplayName;
    private Double basePrice;
    private String priceUnit;
    private Boolean isAvailable;
    private Integer estimatedTime;
    private LocalDateTime createdAt;

    // Constructors
    public ServiceDetailResponse() {
    }

    public ServiceDetailResponse(Long id, String name, String description, String type, String typeDisplayName,
                                  Double basePrice, String priceUnit, Boolean isAvailable, Integer estimatedTime,
                                  LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.typeDisplayName = typeDisplayName;
        this.basePrice = basePrice;
        this.priceUnit = priceUnit;
        this.isAvailable = isAvailable;
        this.estimatedTime = estimatedTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDisplayName() {
        return typeDisplayName;
    }

    public void setTypeDisplayName(String typeDisplayName) {
        this.typeDisplayName = typeDisplayName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ServiceDetailResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", typeDisplayName='" + typeDisplayName + '\'' +
                ", basePrice=" + basePrice +
                ", priceUnit='" + priceUnit + '\'' +
                ", isAvailable=" + isAvailable +
                ", estimatedTime=" + estimatedTime +
                ", createdAt=" + createdAt +
                '}';
    }
}
