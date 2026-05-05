package com.supermarket.model;

import com.supermarket.model.enums.ProductType;
import com.supermarket.observer.StockObserver;
import com.supermarket.observer.StockSubject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp trừu tượng đại diện cho sản phẩm trong siêu thị.
 * Các lớp con: RegularProduct, PromotionProduct, PerishableProduct.
 */
public abstract class Product implements StockSubject {
    private String id;
    private String name;
    private double price;           // Đơn giá (VND)
    private int stockQuantity;
    private String unit;            // Đơn vị tính (cái, kg, lít, hộp, gói...)
    private String categoryId;
    private String supplierId;
    private ProductType productType;
    private LocalDate importDate;   // Ngày nhập hàng

    // Danh sách observer theo dõi tồn kho
    private transient List<StockObserver> observers = new ArrayList<>();

    // Ngưỡng cảnh báo tồn kho thấp
    public static final int LOW_STOCK_THRESHOLD = 10;

    protected Product() {}

    protected Product(String id, String name, double price, int stockQuantity,
                      String unit, String categoryId, String supplierId, ProductType productType) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.productType = productType;
    }

    /** Tính giá bán cuối cùng (mỗi loại SP tính khác nhau). */
    public abstract double calculateFinalPrice();

    /** Mô tả loại sản phẩm (hiển thị). */
    public abstract String getProductTypeDescription();

    /** Cập nhật số lượng tồn kho (+: nhập thêm, -: bán ra). */
    public void updateStock(int quantityChange) {
        this.stockQuantity += quantityChange;
        if (this.stockQuantity < 0) {
            this.stockQuantity = 0;
        }
        notifyObservers();
    }

    /** Kiểm tra tồn kho có dưới ngưỡng cảnh báo không. */
    public boolean isLowStock() {
        return stockQuantity < LOW_STOCK_THRESHOLD;
    }

    // --- Observer ---

    @Override
    public void addObserver(StockObserver observer) {
        if (observers == null) observers = new ArrayList<>();
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(StockObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        if (observers == null) return;
        for (StockObserver observer : observers) {
            observer.onStockChanged(this);
        }
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s - Tồn kho: %d %s",
                id, name,
                com.supermarket.util.CurrencyFormatter.format(price),
                stockQuantity, unit);
    }
}
