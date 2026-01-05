package com.rescue.system.entity;

/**
 * Enum representing the profile approval status of a rescue company
 * Used in UC404 - Company Profile Management
 */
public enum ProfileStatus {
    APPROVED, // Hồ sơ đã được phê duyệt
    PENDING_APPROVAL, // Hồ sơ đang chờ phê duyệt (sau khi cập nhật thông tin quan trọng)
    REJECTED, // Hồ sơ bị từ chối
    INCOMPLETE // Hồ sơ chưa hoàn thiện
}
