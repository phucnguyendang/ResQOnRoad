package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateServiceRequest {
    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "Loại dịch vụ không được để trống")
    private String type; // Enum: TIRE_REPAIR, TIRE_REPLACEMENT, FUEL, TOW, REPAIR, OTHER

    @NotNull(message = "Giá dịch vụ không được để trống")
    @Positive(message = "Giá dịch vụ phải lớn hơn 0")
    private Double basePrice;

    private String priceUnit = "VND"; // VND, USD, per km, per hour

    private Boolean isAvailable = true;

    private Integer estimatedTime; // Thời gian ước tính (phút)

    // Constructors
    public CreateServiceRequest() {
    }

    public CreateServiceRequest(String name, String description, String type, Double basePrice, 
                                String priceUnit, Boolean isAvailable, Integer estimatedTime) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.basePrice = basePrice;
        this.priceUnit = priceUnit;
        this.isAvailable = isAvailable;
        this.estimatedTime = estimatedTime;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "CreateServiceRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", basePrice=" + basePrice +
                ", priceUnit='" + priceUnit + '\'' +
                ", isAvailable=" + isAvailable +
                ", estimatedTime=" + estimatedTime +
                '}';
    }
}
