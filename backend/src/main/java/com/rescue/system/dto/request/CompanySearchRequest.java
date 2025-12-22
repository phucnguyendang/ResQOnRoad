package com.rescue.system.dto.request;

import com.rescue.system.entity.ServiceType;
import jakarta.validation.constraints.*;

import java.util.List;

public class CompanySearchRequest {

    @NotNull(message = "Latitude không được để trống")
    @DecimalMin(value = "-90.0", message = "Latitude phải từ -90 đến 90")
    @DecimalMax(value = "90.0", message = "Latitude phải từ -90 đến 90")
    private Double latitude;

    @NotNull(message = "Longitude không được để trống")
    @DecimalMin(value = "-180.0", message = "Longitude phải từ -180 đến 180")
    @DecimalMax(value = "180.0", message = "Longitude phải từ -180 đến 180")
    private Double longitude;

    @Min(value = 1, message = "Bán kính tìm kiếm tối thiểu là 1km")
    @Max(value = 100, message = "Bán kính tìm kiếm tối đa là 100km")
    private Double maxDistance = 50.0; // Mặc định 50km

    private List<ServiceType> serviceTypes; // Filter theo loại dịch vụ

    @Min(value = 0, message = "Số trang phải >= 0")
    private Integer page = 0;

    @Min(value = 1, message = "Kích thước trang phải >= 1")
    @Max(value = 100, message = "Kích thước trang tối đa là 100")
    private Integer size = 20;

    public CompanySearchRequest() {
    }

    public CompanySearchRequest(Double latitude, Double longitude, Double maxDistance, List<ServiceType> serviceTypes,
            Integer page, Integer size) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxDistance = maxDistance;
        this.serviceTypes = serviceTypes;
        this.page = page;
        this.size = size;
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

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
