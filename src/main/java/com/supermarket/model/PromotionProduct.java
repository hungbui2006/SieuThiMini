package com.supermarket.model;

import com.supermarket.model.enums.ProductType;

import java.time.LocalDate;

/**
 * Sản phẩm khuyến mãi — có tỷ lệ giảm giá và thời hạn khuyến mãi.
 */
public class PromotionProduct extends Product {
    private double discountRate;    // Tỷ lệ giảm giá (ví dụ: 0.15 = 15%)
    private LocalDate promotionStartDate;
    private LocalDate promotionEndDate;

    public PromotionProduct() {
        setProductType(ProductType.PROMOTION);
    }

    public PromotionProduct(String id, String name, double price, int stockQuantity,
                            String unit, String categoryId, String supplierId,
                            double discountRate, LocalDate promotionStartDate,
                            LocalDate promotionEndDate) {
        super(id, name, price, stockQuantity, unit, categoryId, supplierId, ProductType.PROMOTION);
        this.discountRate = discountRate;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
    }

    /** Nếu đang trong thời gian khuyến mãi thì giảm giá, không thì trả giá gốc. */
    @Override
    public double calculateFinalPrice() {
        if (isPromotionActive()) {
            return getPrice() * (1 - discountRate);
        }
        return getPrice();
    }

    /** Kiểm tra khuyến mãi còn hiệu lực không. */
    public boolean isPromotionActive() {
        LocalDate today = LocalDate.now();
        boolean afterStart = (promotionStartDate == null || !today.isBefore(promotionStartDate));
        boolean beforeEnd = (promotionEndDate == null || !today.isAfter(promotionEndDate));
        return afterStart && beforeEnd;
    }

    @Override
    public String getProductTypeDescription() {
        if (isPromotionActive()) {
            return String.format("Khuyến mãi %.0f%% (đến %s)", discountRate * 100, promotionEndDate);
        }
        return "Khuyến mãi (đã hết hạn)";
    }

    // --- Getters and Setters ---

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public LocalDate getPromotionStartDate() {
        return promotionStartDate;
    }

    public void setPromotionStartDate(LocalDate promotionStartDate) {
        this.promotionStartDate = promotionStartDate;
    }

    public LocalDate getPromotionEndDate() {
        return promotionEndDate;
    }

    public void setPromotionEndDate(LocalDate promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }
}
