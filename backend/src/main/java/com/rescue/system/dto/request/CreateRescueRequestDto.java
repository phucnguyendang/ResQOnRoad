package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRescueRequestDto {
    
    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Latitude is required")
    @Range(min = -90, max = 90, message = "Latitude out of range [-90, 90]")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Range(min = -180, max = 180, message = "Longitude out of range [-180, 180]")
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
