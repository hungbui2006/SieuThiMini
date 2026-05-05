package com.supermarket.model.enums;

/**
 * Enum representing user roles for access control.
 * Vai trò người dùng trong hệ thống.
 */
public enum UserRole {
    ADMIN("Quản trị viên"),
    CASHIER("Nhân viên bán hàng");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
