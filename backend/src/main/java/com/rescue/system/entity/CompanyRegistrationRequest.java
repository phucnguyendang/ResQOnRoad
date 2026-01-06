package com.rescue.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "company_registration_requests", indexes = {
        @Index(name = "idx_company_reg_account", columnList = "account_id"),
        @Index(name = "idx_company_reg_status", columnList = "status"),
        @Index(name = "idx_company_reg_submitted", columnList = "submitted_at")
})
public class CompanyRegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompanyRegistrationStatus status = CompanyRegistrationStatus.PENDING;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "service_radius")
    private Double serviceRadius;

    @Column(name = "tax_code", length = 20)
    private String taxCode;

    @Column(name = "hotline", length = 20)
    private String hotline;

    @Column(name = "operating_hours", length = 100)
    private String operatingHours;

    @Column(name = "business_license")
    private String businessLicense;

    @Column(name = "license_document_url", length = 500)
    private String licenseDocumentUrl;

    @Column(length = 500)
    private String description;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_company_id")
    private Long createdCompanyId;

    @Column(name = "reviewed_by_account_id")
    private Long reviewedByAccountId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
