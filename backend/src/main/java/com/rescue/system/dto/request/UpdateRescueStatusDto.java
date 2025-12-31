package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateRescueStatusDto {
    
    @NotNull(message = "Status is required")
    private String status;

    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
