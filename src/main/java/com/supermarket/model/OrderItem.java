package com.supermarket.model;

/**
 * Một dòng sản phẩm trong hóa đơn.
 */
public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double unitPrice;       // Giá tại thời điểm bán (sau giảm giá SP)
    private double originalPrice;   // Giá gốc trước giảm

    public OrderItem() {}

    public OrderItem(String productId, String productName, int quantity,
                     double unitPrice, double originalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.originalPrice = originalPrice;
    }

    /** Tính thành tiền = số lượng * đơn giá. */
    public double getSubTotal() {
        return quantity * unitPrice;
    }

    /** Tính số tiền được giảm của dòng này. */
    public double getDiscountAmount() {
        return (originalPrice - unitPrice) * quantity;
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
}
