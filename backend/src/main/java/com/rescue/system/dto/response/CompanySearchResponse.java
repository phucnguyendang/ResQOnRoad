package com.rescue.system.dto.response;

import java.util.List;

public class CompanySearchResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Double distance; // Khoảng cách tính từ vị trí người dùng (km)
    private Double averageRating;
    private Integer totalReviews;
    private Boolean isAvailable;
    private Boolean isVerified;
    private String description;
    private List<ServiceSummary> services;
    private Double latitude;
    private Double longitude;

    public CompanySearchResponse() {
    }

    public CompanySearchResponse(Long id, String name, String address, String phone, String email, Double distance,
            Double averageRating, Integer totalReviews, Boolean isAvailable, Boolean isVerified, String description,
            List<ServiceSummary> services, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.distance = distance;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.isAvailable = isAvailable;
        this.isVerified = isVerified;
        this.description = description;
        this.services = services;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CompanySearchResponseBuilder builder() {
        return new CompanySearchResponseBuilder();
    }

    public static class CompanySearchResponseBuilder {
        private Long id;
        private String name;
        private String address;
        private String phone;
        private String email;
        private Double distance;
        private Double averageRating;
        private Integer totalReviews;
        private Boolean isAvailable;
        private Boolean isVerified;
        private String description;
        private List<ServiceSummary> services;
        private Double latitude;
        private Double longitude;

        CompanySearchResponseBuilder() {
        }

        public CompanySearchResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CompanySearchResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CompanySearchResponseBuilder address(String address) {
            this.address = address;
            return this;
        }

        public CompanySearchResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CompanySearchResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CompanySearchResponseBuilder distance(Double distance) {
            this.distance = distance;
            return this;
        }

        public CompanySearchResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public CompanySearchResponseBuilder totalReviews(Integer totalReviews) {
            this.totalReviews = totalReviews;
            return this;
        }

        public CompanySearchResponseBuilder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public CompanySearchResponseBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public CompanySearchResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CompanySearchResponseBuilder services(List<ServiceSummary> services) {
            this.services = services;
            return this;
        }

        public CompanySearchResponseBuilder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public CompanySearchResponseBuilder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public CompanySearchResponse build() {
            return new CompanySearchResponse(id, name, address, phone, email, distance, averageRating, totalReviews,
                    isAvailable, isVerified, description, services, latitude, longitude);
        }
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ServiceSummary> getServices() {
        return services;
    }

    public void setServices(List<ServiceSummary> services) {
        this.services = services;
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
}
