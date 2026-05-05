package com.supermarket.model;

import com.supermarket.model.enums.UserRole;

/**
 * Lớp trừu tượng đại diện cho người dùng hệ thống (Admin hoặc Nhân viên).
 */
public abstract class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private UserRole role;
    private boolean active;

    protected User() {
        this.active = true;
    }

    protected User(String id, String username, String password, String fullName,
                   String phone, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.active = true;
    }

    /** Kiểm tra mật khẩu đăng nhập. */
    public boolean authenticate(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    /** Mô tả vai trò của người dùng. */
    public abstract String getRoleDescription();

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", id, fullName, username, role.getDisplayName());
    }
}
