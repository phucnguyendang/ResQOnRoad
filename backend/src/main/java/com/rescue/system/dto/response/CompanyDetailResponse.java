package com.rescue.system.dto.response;

import java.util.List;
import java.util.Objects;

public class CompanyDetailResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Double latitude;
    private Double longitude;
    private Double serviceRadius;
    private Boolean isActive;
    private Boolean isVerified;
    private Double averageRating;
    private Integer totalReviews;
    private String description;
    private String businessLicense;
    private List<ServiceSummary> services;
    private List<ReviewDetail> reviews;

    // Constructors
    public CompanyDetailResponse() {
    }

    public CompanyDetailResponse(Long id, String name, String address, String phone, String email, Double latitude,
            Double longitude, Double serviceRadius, Boolean isActive, Boolean isVerified, Double averageRating,
            Integer totalReviews, String description, String businessLicense, List<ServiceSummary> services,
            List<ReviewDetail> reviews) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.serviceRadius = serviceRadius;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.description = description;
        this.businessLicense = businessLicense;
        this.services = services;
        this.reviews = reviews;
    }

    // Static builder method
    public static CompanyDetailResponseBuilder builder() {
        return new CompanyDetailResponseBuilder();
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
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

    public List<ServiceSummary> getServices() {
        return services;
    }

    public void setServices(List<ServiceSummary> services) {
        this.services = services;
    }

    public List<ReviewDetail> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDetail> reviews) {
        this.reviews = reviews;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CompanyDetailResponse that = (CompanyDetailResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phone, email);
    }

    @Override
    public String toString() {
        return "CompanyDetailResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", serviceRadius=" + serviceRadius +
                ", isActive=" + isActive +
                ", isVerified=" + isVerified +
                ", averageRating=" + averageRating +
                ", totalReviews=" + totalReviews +
                '}';
    }

    // Builder class
    public static class CompanyDetailResponseBuilder {
        private Long id;
        private String name;
        private String address;
        private String phone;
        private String email;
        private Double latitude;
        private Double longitude;
        private Double serviceRadius;
        private Boolean isActive;
        private Boolean isVerified;
        private Double averageRating;
        private Integer totalReviews;
        private String description;
        private String businessLicense;
        private List<ServiceSummary> services;
        private List<ReviewDetail> reviews;

        CompanyDetailResponseBuilder() {
        }

        public CompanyDetailResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CompanyDetailResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CompanyDetailResponseBuilder address(String address) {
            this.address = address;
            return this;
        }

        public CompanyDetailResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CompanyDetailResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CompanyDetailResponseBuilder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public CompanyDetailResponseBuilder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public CompanyDetailResponseBuilder serviceRadius(Double serviceRadius) {
            this.serviceRadius = serviceRadius;
            return this;
        }

        public CompanyDetailResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CompanyDetailResponseBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public CompanyDetailResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public CompanyDetailResponseBuilder totalReviews(Integer totalReviews) {
            this.totalReviews = totalReviews;
            return this;
        }

        public CompanyDetailResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CompanyDetailResponseBuilder businessLicense(String businessLicense) {
            this.businessLicense = businessLicense;
            return this;
        }

        public CompanyDetailResponseBuilder services(List<ServiceSummary> services) {
            this.services = services;
            return this;
        }

        public CompanyDetailResponseBuilder reviews(List<ReviewDetail> reviews) {
            this.reviews = reviews;
            return this;
        }

        public CompanyDetailResponse build() {
            return new CompanyDetailResponse(id, name, address, phone, email, latitude, longitude, serviceRadius,
                    isActive, isVerified, averageRating, totalReviews, description, businessLicense, services, reviews);
        }
    }
}
