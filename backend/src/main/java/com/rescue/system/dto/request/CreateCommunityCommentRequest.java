package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new community comment
 * Part of UC103: Community Support and Consultation
 */
public class CreateCommunityCommentRequest {

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(min = 1, max = 5000, message = "Nội dung bình luận phải từ 1 đến 5000 ký tự")
    private String content;

    private Long parentCommentId;

    // Constructors
    public CreateCommunityCommentRequest() {
    }

    public CreateCommunityCommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
