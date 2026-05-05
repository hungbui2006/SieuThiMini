package com.supermarket.model;

import com.supermarket.model.enums.UserRole;

/**
 * Quản trị viên — có toàn quyền quản lý hệ thống.
 */
public class Admin extends User {

    public Admin() {
        setRole(UserRole.ADMIN);
    }

    public Admin(String id, String username, String password, String fullName, String phone) {
        super(id, username, password, fullName, phone, UserRole.ADMIN);
    }

    @Override
    public String getRoleDescription() {
        return "Quản trị viên - Toàn quyền quản lý hệ thống";
    }
}
