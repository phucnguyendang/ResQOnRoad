package com.rescue.system.entity;

/**
 * Enum representing the status of moderatable content
 * Part of UC405: Content Moderation (Kiểm duyệt nội dung)
 */
public enum ContentStatus {
    /**
     * Content is pending review (Chờ duyệt)
     */
    PENDING,

    /**
     * Content has been approved (Đã phê duyệt)
     */
    APPROVED,

    /**
     * Content has been rejected (Bị từ chối)
     */
    REJECTED,

    /**
     * Content has been removed (Bị gỡ bỏ)
     */
    REMOVED
}
