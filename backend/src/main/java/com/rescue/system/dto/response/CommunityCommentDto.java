package com.rescue.system.dto.response;

import java.time.Instant;

/**
 * DTO for community comment response
 * Part of UC103: Community Support and Consultation
 */
public class CommunityCommentDto {

    private Long id;
    private String content;
    private CommunityPostDto.AuthorInfo author;
    private Long postId;
    private Long parentCommentId;
    private Boolean isHelpful;
    private Integer helpfulCount;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructors
    public CommunityCommentDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommunityPostDto.AuthorInfo getAuthor() {
        return author;
    }

    public void setAuthor(CommunityPostDto.AuthorInfo author) {
        this.author = author;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Boolean getIsHelpful() {
        return isHelpful;
    }

    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
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
}
