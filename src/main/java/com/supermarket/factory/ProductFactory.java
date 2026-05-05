package com.supermarket.factory;

import com.supermarket.model.*;
import com.supermarket.model.enums.ProductType;

import java.time.LocalDate;

/**
 * Lớp Factory tạo sản phẩm theo loại (thường, khuyến mãi, có HSD).
 */
public class ProductFactory {

    /** Tạo sản phẩm theo loại với các thuộc tính chung. */
    public static Product createProduct(ProductType type, String id, String name,
                                        double price, int stockQuantity, String unit,
                                        String categoryId, String supplierId) {
        Product product;
        switch (type) {
            case REGULAR:
                product = new RegularProduct(id, name, price, stockQuantity, unit, categoryId, supplierId);
                break;

            case PROMOTION:
                // Default promotion: 10% discount, starts today, ends in 30 days
                product = new PromotionProduct(id, name, price, stockQuantity, unit, categoryId, supplierId,
                        0.10, LocalDate.now(), LocalDate.now().plusDays(30));
                break;

            case PERISHABLE:
                // Default: manufactured today, expires in 30 days
                product = new PerishableProduct(id, name, price, stockQuantity, unit, categoryId, supplierId,
                        LocalDate.now(), LocalDate.now().plusDays(30));
                break;

            default:
                throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + type);
        }
        product.setImportDate(LocalDate.now());
        return product;
    }

    /**
     * Creates a PromotionProduct with explicit promotion details.
     */
    public static PromotionProduct createPromotionProduct(String id, String name,
                                                           double price, int stockQuantity, String unit,
                                                           String categoryId, String supplierId,
                                                           double discountRate,
                                                           LocalDate startDate, LocalDate endDate) {
        PromotionProduct p = new PromotionProduct(id, name, price, stockQuantity, unit, categoryId, supplierId,
                discountRate, startDate, endDate);
        p.setImportDate(LocalDate.now());
        return p;
    }

    /**
     * Creates a PerishableProduct with explicit manufacturing and expiry dates.
     */
    public static PerishableProduct createPerishableProduct(String id, String name,
                                                             double price, int stockQuantity, String unit,
                                                             String categoryId, String supplierId,
                                                             LocalDate mfgDate, LocalDate expiryDate) {
        PerishableProduct p = new PerishableProduct(id, name, price, stockQuantity, unit, categoryId, supplierId,
                mfgDate, expiryDate);
        p.setImportDate(LocalDate.now());
        return p;
    }
}
