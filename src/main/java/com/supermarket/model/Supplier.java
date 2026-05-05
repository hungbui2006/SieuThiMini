package com.supermarket.model;

/**
 * Nhà cung cấp hàng hóa cho siêu thị.
 */
public class Supplier {
    private String id;
    private String name;
    private String contactPhone;
    private String address;
    private String email;

    public Supplier() {}

    public Supplier(String id, String name, String contactPhone, String address, String email) {
        this.id = id;
        this.name = name;
        this.contactPhone = contactPhone;
        this.address = address;
        this.email = email;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - SĐT: %s", id, name, contactPhone);
    }
}
