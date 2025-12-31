package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class CreateRescueRequestDto {
    
    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude out of range [-90, 90]")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude out of range [-90, 90]")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude out of range [-180, 180]")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude out of range [-180, 180]")
    private Double longitude;
    
    private String description;

    private String serviceType;

    public CreateRescueRequestDto() {
    }

    public CreateRescueRequestDto(String location, Double latitude, Double longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
