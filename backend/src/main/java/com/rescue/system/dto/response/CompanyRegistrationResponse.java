package com.rescue.system.dto.response;

import com.rescue.system.entity.CompanyRegistrationStatus;
import java.time.LocalDateTime;

public class CompanyRegistrationResponse {

    private Long id;
    private CompanyRegistrationStatus status;

    private String name;
    private String address;
    private String phone;
    private String email;
    private Double latitude;
    private Double longitude;
    private Double serviceRadius;
    private String taxCode;
    private String hotline;
    private String operatingHours;
    private String businessLicense;
    private String licenseDocumentUrl;
    private String description;

    private String rejectionReason;
    private Long createdCompanyId;
    private Long reviewedByAccountId;
    private LocalDateTime reviewedAt;
    private LocalDateTime submittedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanyRegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyRegistrationStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Double getServiceRadius() {
        return serviceRadius;
    }

    public void setServiceRadius(Double serviceRadius) {
        this.serviceRadius = serviceRadius;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getLicenseDocumentUrl() {
        return licenseDocumentUrl;
    }

    public void setLicenseDocumentUrl(String licenseDocumentUrl) {
        this.licenseDocumentUrl = licenseDocumentUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getCreatedCompanyId() {
        return createdCompanyId;
    }

    public void setCreatedCompanyId(Long createdCompanyId) {
        this.createdCompanyId = createdCompanyId;
    }

    public Long getReviewedByAccountId() {
        return reviewedByAccountId;
    }

    public void setReviewedByAccountId(Long reviewedByAccountId) {
        this.reviewedByAccountId = reviewedByAccountId;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
