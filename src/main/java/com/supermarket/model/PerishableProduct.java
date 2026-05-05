package com.supermarket.model;

import com.supermarket.model.enums.ProductType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Sản phẩm có hạn sử dụng — tự động giảm giá khi gần hết hạn.
 */
public class PerishableProduct extends Product {
    private LocalDate expiryDate;
    private LocalDate manufacturingDate;

    // SP gần hết hạn (3 ngày) được giảm 30%
    private static final int NEAR_EXPIRY_DAYS = 3;
    private static final double NEAR_EXPIRY_DISCOUNT = 0.30;

    public PerishableProduct() {
        setProductType(ProductType.PERISHABLE);
    }

    public PerishableProduct(String id, String name, double price, int stockQuantity,
                             String unit, String categoryId, String supplierId,
                             LocalDate manufacturingDate, LocalDate expiryDate) {
        super(id, name, price, stockQuantity, unit, categoryId, supplierId, ProductType.PERISHABLE);
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
    }

    /** Tính giá bán: hết hạn = 0, gần hết hạn = giảm 30%, còn hạn = giá gốc. */
    @Override
    public double calculateFinalPrice() {
        if (isExpired()) {
            return 0; // Hết hạn không bán
        }
        if (isNearExpiry()) {
            return getPrice() * (1 - NEAR_EXPIRY_DISCOUNT);
        }
        return getPrice();
    }

    /** Kiểm tra đã hết hạn chưa. */
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    /** Kiểm tra có gần hết hạn không (trong 3 ngày). */
    public boolean isNearExpiry() {
        if (expiryDate == null) return false;
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        return daysUntilExpiry >= 0 && daysUntilExpiry <= NEAR_EXPIRY_DAYS;
    }

    /** Số ngày còn lại trước khi hết hạn. */
    public long getDaysUntilExpiry() {
        if (expiryDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    @Override
    public String getProductTypeDescription() {
        if (isExpired()) {
            return "⚠ ĐÃ HẾT HẠN";
        }
        if (isNearExpiry()) {
            return String.format("⚠ Sắp hết hạn (%d ngày) - Giảm %.0f%%",
                    getDaysUntilExpiry(), NEAR_EXPIRY_DISCOUNT * 100);
        }
        return String.format("HSD: %s (%d ngày còn lại)", expiryDate, getDaysUntilExpiry());
    }

    // --- Getters and Setters ---

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }
}
