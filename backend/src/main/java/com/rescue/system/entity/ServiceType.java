package com.rescue.system.entity;

/**
 * Enum định nghĩa các loại dịch vụ cứu hộ
 */
public enum ServiceType {
    TOW_TRUCK("Cẩu xe"),
    TIRE_CHANGE("Vá lốp"),
    BATTERY_JUMP("Cứu hộ ắc quy"),
    FUEL_DELIVERY("Giao nhiên liệu"),
    LOCKOUT("Mở khóa xe"),
    WINCH_OUT("Kéo xe bị sa lầy"),
    ACCIDENT_RECOVERY("Cứu hộ tai nạn"),
    MECHANICAL_REPAIR("Sửa chữa cơ bản");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
