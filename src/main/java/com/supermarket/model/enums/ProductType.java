package com.supermarket.model.enums;

/**
 * Enum representing the different types of products in the system.
 * Used by the Factory Pattern to instantiate the correct product subclass.
 */
public enum ProductType {
    REGULAR("Sản phẩm thường"),
    PROMOTION("Sản phẩm khuyến mãi"),
    PERISHABLE("Sản phẩm có hạn sử dụng");

    private final String displayName;

    ProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
