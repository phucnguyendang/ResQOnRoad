package com.rescue.system.dto.request;

import jakarta.validation.constraints.*;

public class UpdateCompanyAdminRequest {

    @Size(max = 200, message = "Tên công ty không vượt quá 200 ký tự")
    private String name;

    private String address;

    @Size(max = 20, message = "Số điện thoại không vượt quá 20 ký tự")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không vượt quá 100 ký tự")
    private String email;

    private Double latitude;

    private Double longitude;

    @Positive(message = "Bán kính hoạt động phải là số dương")
    private Double serviceRadius;

    @Size(max = 20, message = "Mã số thuế không vượt quá 20 ký tự")
    private String taxCode;

    @Size(max = 20, message = "Số hotline không vượt quá 20 ký tự")
    private String hotline;

    @Size(max = 100, message = "Giờ hoạt động không vượt quá 100 ký tự")
    private String operatingHours;

    @Size(max = 500, message = "URL giấy phép không vượt quá 500 ký tự")
    private String licenseDocumentUrl;

    @Size(max = 500, message = "Mô tả không vượt quá 500 ký tự")
    private String description;

    private String businessLicense;

    // Getters and Setters
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

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }
}
