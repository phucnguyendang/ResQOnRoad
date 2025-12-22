package com.rescue.system.dto.response;

import com.rescue.system.entity.RescueRequest;
import com.rescue.system.entity.RescueStatus;
import java.time.Instant;

public class RescueRequestDto {

    private Long id;
    private Long userId;
    private String userName;
    private String userPhoneNumber;
    private Long companyId;
    private String companyName;
    private String companyPhoneNumber;
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private String serviceType;
    private RescueStatus status;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    public RescueRequestDto() {
    }

    public RescueRequestDto(RescueRequest request) {
        this.id = request.getId();
        this.userId = request.getUser().getId();
        this.userName = request.getUser().getFullName();
        this.userPhoneNumber = request.getUser().getPhoneNumber();
        
        if (request.getCompany() != null) {
            this.companyId = request.getCompany().getId();
            this.companyName = request.getCompany().getFullName();
            this.companyPhoneNumber = request.getCompany().getPhoneNumber();
        }
        
        this.location = request.getLocation();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.description = request.getDescription();
        this.serviceType = request.getServiceType();
        this.status = request.getStatus();
        this.rejectionReason = request.getRejectionReason();
        this.createdAt = request.getCreatedAt();
        this.updatedAt = request.getUpdatedAt();
        this.completedAt = request.getCompletedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyPhoneNumber() {
        return companyPhoneNumber;
    }

    public void setCompanyPhoneNumber(String companyPhoneNumber) {
        this.companyPhoneNumber = companyPhoneNumber;
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

    public RescueStatus getStatus() {
        return status;
    }

    public void setStatus(RescueStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
