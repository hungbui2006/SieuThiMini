package com.supermarket.model;

/**
 * Represents a single line item in a goods import receipt.
 * Một dòng sản phẩm trong phiếu nhập hàng.
 */
public class ImportItem {
    private String productId;
    private String productName;
    private int quantity;
    private double importPrice; // Giá nhập

    public ImportItem() {}

    public ImportItem(String productId, String productName, int quantity, double importPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.importPrice = importPrice;
    }

    public double getSubTotal() {
        return quantity * importPrice;
    }

    // --- Getters and Setters ---

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }
}
