package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for moderation action
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
public class ModerationActionRequest {

    @NotNull(message = "Action is required")
    @NotBlank(message = "Action cannot be blank")
    private String action; // APPROVE, REJECT, REMOVE

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason; // Required for REJECT and REMOVE actions

    public ModerationActionRequest() {
    }

    public ModerationActionRequest(String action, String reason) {
        this.action = action;
        this.reason = reason;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
