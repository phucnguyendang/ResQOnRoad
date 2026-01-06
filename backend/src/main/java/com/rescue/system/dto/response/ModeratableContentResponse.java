package com.rescue.system.dto.response;

import com.rescue.system.entity.ContentStatus;
import com.rescue.system.entity.ContentType;
import com.rescue.system.entity.ModeratableContent;

import java.time.LocalDateTime;

/**
 * Response DTO for moderatable content
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
public class ModeratableContentResponse {

    private Long id;
    private ContentType contentType;
    private Long referenceId;
    private Long authorId;
    private String authorUsername;
    private String authorFullName;
    private String contentTitle;
    private String contentText;
    private String contentImage;
    private ContentStatus status;
    private Long moderatorId;
    private String moderatorUsername;
    private String moderationReason;
    private LocalDateTime createdAt;
    private LocalDateTime moderatedAt;

    public ModeratableContentResponse() {
    }

    /**
     * Factory method to create response from entity
     */
    public static ModeratableContentResponse fromEntity(ModeratableContent content) {
        ModeratableContentResponse response = new ModeratableContentResponse();
        response.setId(content.getId());
        response.setContentType(content.getContentType());
        response.setReferenceId(content.getReferenceId());

        if (content.getAuthor() != null) {
            response.setAuthorId(content.getAuthor().getId());
            response.setAuthorUsername(content.getAuthor().getUsername());
            response.setAuthorFullName(content.getAuthor().getFullName());
        }

        response.setContentTitle(content.getContentTitle());
        response.setContentText(content.getContentText());
        response.setContentImage(content.getContentImage());
        response.setStatus(content.getStatus());

        if (content.getModerator() != null) {
            response.setModeratorId(content.getModerator().getId());
            response.setModeratorUsername(content.getModerator().getUsername());
        }

        response.setModerationReason(content.getModerationReason());
        response.setCreatedAt(content.getCreatedAt());
        response.setModeratedAt(content.getModeratedAt());

        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public Long getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Long moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getModeratorUsername() {
        return moderatorUsername;
    }

    public void setModeratorUsername(String moderatorUsername) {
        this.moderatorUsername = moderatorUsername;
    }

    public String getModerationReason() {
        return moderationReason;
    }

    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModeratedAt() {
        return moderatedAt;
    }

    public void setModeratedAt(LocalDateTime moderatedAt) {
        this.moderatedAt = moderatedAt;
    }
}
