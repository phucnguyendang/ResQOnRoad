package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RejectRescueRequestDto {
    
    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;

    public RejectRescueRequestDto() {
    }

    public RejectRescueRequestDto(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
