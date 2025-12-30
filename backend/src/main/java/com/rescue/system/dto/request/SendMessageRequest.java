package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for sending a message
 * UC101 - Step 3: User sends message
 */
public class SendMessageRequest {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotBlank(message = "Message content is required")
    private String content;

    private String attachmentUrl;

    private String attachmentType;

    // Constructors
    public SendMessageRequest() {
    }

    public SendMessageRequest(Long requestId, String content) {
        this.requestId = requestId;
        this.content = content;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }
}
