package com.rescue.system.entity;

public enum RescueStatus {
    PENDING_CONFIRMATION("Đang chờ xác nhận"),
    ACCEPTED("Đã tiếp nhận"),
    IN_TRANSIT("Đang di chuyển"),
    IN_PROGRESS("Đang xử lý"),
    COMPLETED("Hoàn thành"),
    REJECTED_BY_COMPANY("Bị từ chối bởi công ty cứu hộ"),
    CANCELLED_BY_USER("Đã hủy bởi người dùng");

    private final String displayName;

    RescueStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
