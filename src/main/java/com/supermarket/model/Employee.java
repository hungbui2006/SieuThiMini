package com.supermarket.model;

import com.supermarket.model.enums.UserRole;

/**
 * Nhân viên bán hàng — có lương cơ bản và thưởng doanh số.
 */
public class Employee extends User {
    private double baseSalary;          // Lương cơ bản
    private double salesCommissionRate; // Tỷ lệ hoa hồng (vd: 0.02 = 2%)
    private double totalSalesAmount;    // Tổng doanh số tích lũy

    public Employee() {
        setRole(UserRole.CASHIER);
    }

    public Employee(String id, String username, String password, String fullName,
                    String phone, double baseSalary, double salesCommissionRate) {
        super(id, username, password, fullName, phone, UserRole.CASHIER);
        this.baseSalary = baseSalary;
        this.salesCommissionRate = salesCommissionRate;
        this.totalSalesAmount = 0;
    }

    /** Tính tổng lương = lương CB + hoa hồng. */
    public double calculateTotalSalary() {
        return baseSalary + calculateCommission();
    }

    /** Tính hoa hồng từ doanh số bán hàng. */
    public double calculateCommission() {
        return totalSalesAmount * salesCommissionRate;
    }

    /** Ghi nhận doanh số bán hàng. */
    public void addSaleAmount(double saleAmount) {
        this.totalSalesAmount += saleAmount;
    }

    @Override
    public String getRoleDescription() {
        return "Nhân viên bán hàng - Thu ngân";
    }

    // --- Getters and Setters ---

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public double getSalesCommissionRate() {
        return salesCommissionRate;
    }

    public void setSalesCommissionRate(double salesCommissionRate) {
        this.salesCommissionRate = salesCommissionRate;
    }

    public double getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(double totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }
}
