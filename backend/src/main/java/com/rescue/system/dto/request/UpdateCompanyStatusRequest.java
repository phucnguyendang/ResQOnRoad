package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateCompanyStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private String status; // ACTIVE, LOCKED, DELETED

    private String reason; // Lý do khóa/xóa tài khoản

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
