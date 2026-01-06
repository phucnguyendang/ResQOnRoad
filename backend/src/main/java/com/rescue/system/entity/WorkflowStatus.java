package com.rescue.system.entity;

/**
 * Enum định nghĩa các trạng thái của quy trình cứu hộ
 */
public enum WorkflowStatus {
    PENDING("Đang chờ xử lý"),
    ASSIGNED("Đã phân công"),
    DISPATCHED("Đã điều phối"),
    IN_TRANSIT("Đang di chuyển đến hiện trường"),
    ON_SITE("Đã đến hiện trường"),
    IN_PROGRESS("Đang thực hiện cứu hộ"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy"),
    FAILED("Thất bại");

    private final String displayName;

    WorkflowStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
