package com.rescue.system.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * DTO for community post response
 * Part of UC103: Community Support and Consultation
 */
public class CommunityPostDto {

    private Long id;
    private String title;
    private String content;
    private AuthorInfo author;
    private String incidentType;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imageBase64;
    private Integer viewCount;
    private Boolean isResolved;
    private Integer commentCount;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CommunityCommentDto> comments;

    // Constructors
    public CommunityPostDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public AuthorInfo getAuthor() {
        return author;
    }

    public void setAuthor(AuthorInfo author) {
        this.author = author;
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

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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

    public List<CommunityCommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommunityCommentDto> comments) {
        this.comments = comments;
    }

    /**
     * Inner class for author information
     */
    public static class AuthorInfo {
        private Long id;
        private String username;
        private String fullName;
        private String avatarBase64;

        public AuthorInfo() {
        }

        public AuthorInfo(Long id, String username, String fullName, String avatarBase64) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.avatarBase64 = avatarBase64;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAvatarBase64() {
            return avatarBase64;
        }

        public void setAvatarBase64(String avatarBase64) {
            this.avatarBase64 = avatarBase64;
        }
    }
}
