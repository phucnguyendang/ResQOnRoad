package com.rescue.system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing content that can be moderated by admin
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 * 
 * This entity tracks the moderation status of various content types
 * (posts, comments, reviews) in a centralized manner.
 */
@Entity
@Table(name = "moderatable_contents", indexes = {
        @Index(name = "idx_content_status", columnList = "status"),
        @Index(name = "idx_content_type", columnList = "content_type"),
        @Index(name = "idx_content_reference", columnList = "content_type, reference_id")
})
public class ModeratableContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of content (POST, COMMENT, REVIEW)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ContentType contentType;

    /**
     * Reference ID to the actual content (post_id, comment_id, review_id)
     */
    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    /**
     * The author/owner of this content
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    /**
     * Snapshot of the content text at time of submission
     */
    @Column(name = "content_text", nullable = false, columnDefinition = "TEXT")
    private String contentText;

    /**
     * Title (if applicable, e.g., for posts)
     */
    @Column(name = "content_title", length = 500)
    private String contentTitle;

    /**
     * Image in base64 format (if applicable)
     */
    @Column(name = "content_image", columnDefinition = "TEXT")
    private String contentImage;

    /**
     * Current moderation status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContentStatus status = ContentStatus.PENDING;

    /**
     * Admin who moderated this content (null if pending)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private Account moderator;

    /**
     * Reason for rejection or removal (required when rejecting/removing)
     */
    @Column(name = "moderation_reason", length = 1000)
    private String moderationReason;

    /**
     * Timestamp when the content was submitted for moderation
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the moderation status was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Timestamp when moderation decision was made
     */
    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    // Constructors
    public ModeratableContent() {
    }

    public ModeratableContent(ContentType contentType, Long referenceId, Account author,
            String contentText, String contentTitle, String contentImage) {
        this.contentType = contentType;
        this.referenceId = referenceId;
        this.author = author;
        this.contentText = contentText;
        this.contentTitle = contentTitle;
        this.contentImage = contentImage;
        this.status = ContentStatus.PENDING;
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

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
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

    public Account getModerator() {
        return moderator;
    }

    public void setModerator(Account moderator) {
        this.moderator = moderator;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getModeratedAt() {
        return moderatedAt;
    }

    public void setModeratedAt(LocalDateTime moderatedAt) {
        this.moderatedAt = moderatedAt;
    }

    @Override
    public String toString() {
        return "ModeratableContent{" +
                "id=" + id +
                ", contentType=" + contentType +
                ", referenceId=" + referenceId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
