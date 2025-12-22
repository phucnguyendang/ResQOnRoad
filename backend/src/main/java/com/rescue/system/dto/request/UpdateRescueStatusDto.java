package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateRescueStatusDto {
    
    @NotNull(message = "Status is required")
    private String status;

    private String reason;

    public UpdateRescueStatusDto() {
    }

    public UpdateRescueStatusDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
