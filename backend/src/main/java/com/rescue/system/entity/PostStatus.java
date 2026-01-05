package com.rescue.system.entity;

/**
 * Trạng thái của bài đăng trong cộng đồng
 * 
 * OPEN: Bài đăng mở, đang chờ tư vấn từ cộng đồng
 * RESOLVED: Bài đăng đã được giải quyết
 * CLOSED: Bài đăng đã đóng lại, không nhận bình luận mới
 */
public enum PostStatus {
    OPEN("Mở"),
    RESOLVED("Đã giải quyết"),
    CLOSED("Đã đóng");

    private final String displayName;

    PostStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
