package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new community post
 * Part of UC103: Community Support and Consultation
 */
public class CreateCommunityPostRequest {

    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    @Size(min = 5, max = 500, message = "Tiêu đề phải từ 5 đến 500 ký tự")
    private String title;

    @NotBlank(message = "Nội dung bài viết không được để trống")
    @Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự")
    private String content;

    private String incidentType;

    private String location;

    private Double latitude;

    private Double longitude;

    private String imageBase64;

    // Constructors
    public CreateCommunityPostRequest() {
    }

    public CreateCommunityPostRequest(String title, String content) {
        this.title = title;
        this.content = content;
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
}
