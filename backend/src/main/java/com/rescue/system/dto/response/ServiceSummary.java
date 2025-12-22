package com.rescue.system.dto.response;

import java.util.Objects;

public class ServiceSummary {
    private Long id;
    private String name;
    private String type;
    private String typeDisplayName;
    private Double basePrice;
    private String priceUnit;
    private Boolean isAvailable;
    private Integer estimatedTime;

    // Constructors
    public ServiceSummary() {
    }

    public ServiceSummary(Long id, String name, String type, String typeDisplayName, Double basePrice, String priceUnit,
            Boolean isAvailable, Integer estimatedTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeDisplayName = typeDisplayName;
        this.basePrice = basePrice;
        this.priceUnit = priceUnit;
        this.isAvailable = isAvailable;
        this.estimatedTime = estimatedTime;
    }

    // Static builder method
    public static ServiceSummaryBuilder builder() {
        return new ServiceSummaryBuilder();
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

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ServiceSummary that = (ServiceSummary) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }

    @Override
    public String toString() {
        return "ServiceSummary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", typeDisplayName='" + typeDisplayName + '\'' +
                ", basePrice=" + basePrice +
                ", priceUnit='" + priceUnit + '\'' +
                ", isAvailable=" + isAvailable +
                ", estimatedTime=" + estimatedTime +
                '}';
    }

    // Builder class
    public static class ServiceSummaryBuilder {
        private Long id;
        private String name;
        private String type;
        private String typeDisplayName;
        private Double basePrice;
        private String priceUnit;
        private Boolean isAvailable;
        private Integer estimatedTime;

        ServiceSummaryBuilder() {
        }

        public ServiceSummaryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ServiceSummaryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ServiceSummaryBuilder type(String type) {
            this.type = type;
            return this;
        }

        public ServiceSummaryBuilder typeDisplayName(String typeDisplayName) {
            this.typeDisplayName = typeDisplayName;
            return this;
        }

        public ServiceSummaryBuilder basePrice(Double basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public ServiceSummaryBuilder priceUnit(String priceUnit) {
            this.priceUnit = priceUnit;
            return this;
        }

        public ServiceSummaryBuilder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public ServiceSummaryBuilder estimatedTime(Integer estimatedTime) {
            this.estimatedTime = estimatedTime;
            return this;
        }

        public ServiceSummary build() {
            return new ServiceSummary(id, name, type, typeDisplayName, basePrice, priceUnit, isAvailable,
                    estimatedTime);
        }
    }
}
