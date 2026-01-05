package com.rescue.system.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a community post
 * Part of UC103: Community Support and Consultation
 */
public class UpdateCommunityPostRequest {

    @Size(min = 5, max = 500, message = "Tiêu đề phải từ 5 đến 500 ký tự")
    private String title;

    @Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự")
    private String content;

    private String incidentType;

    private String location;

    private Double latitude;

    private Double longitude;

    private String imageBase64;

    private Boolean isResolved;

    // Constructors
    public UpdateCommunityPostRequest() {
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }
}
