package com.supermarket.model;

import com.supermarket.model.enums.ProductType;

/**
 * Sản phẩm thường — giá bán = giá gốc, không giảm giá.
 */
public class RegularProduct extends Product {

    public RegularProduct() {
        setProductType(ProductType.REGULAR);
    }

    public RegularProduct(String id, String name, double price, int stockQuantity,
                          String unit, String categoryId, String supplierId) {
        super(id, name, price, stockQuantity, unit, categoryId, supplierId, ProductType.REGULAR);
    }

    /**
     * Regular products sell at their base price — no discounts applied.
     */
    @Override
    public double calculateFinalPrice() {
        return getPrice();
    }

    @Override
    public String getProductTypeDescription() {
        return "Sản phẩm thường";
    }
}
