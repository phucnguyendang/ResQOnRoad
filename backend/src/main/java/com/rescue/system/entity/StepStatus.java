package com.rescue.system.entity;

/**
 * Enum định nghĩa các trạng thái của bước xử lý
 */
public enum StepStatus {
    PENDING("Đang chờ"),
    IN_PROGRESS("Đang thực hiện"),
    COMPLETED("Hoàn thành"),
    SKIPPED("Bỏ qua"),
    FAILED("Thất bại");

    private final String displayName;

    StepStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
