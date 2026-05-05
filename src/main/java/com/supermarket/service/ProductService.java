package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.model.*;
import com.supermarket.model.enums.ProductType;
import com.supermarket.observer.StockObserver;
import com.supermarket.repository.ProductRepository;

import java.util.List;

/**
 * Service xử lý CRUD sản phẩm và quản lý tồn kho.
 * Theo dõi tồn kho thấp thông qua Observer.
 */
public class ProductService implements StockObserver {

    private final ProductRepository productRepo;

    public ProductService() {
        this.productRepo = DataStore.getInstance().getProductRepository();
    }

    /** Thêm sản phẩm mới và đăng ký theo dõi tồn kho. */
    public void addProduct(Product product) {
        product.addObserver(this);
        productRepo.save(product);
    }

    /** Lấy sản phẩm theo mã. */
    public Product getProduct(String id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy sản phẩm với mã: " + id));
    }

    /** Lấy tất cả sản phẩm. */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    /** Cập nhật sản phẩm. */
    public void updateProduct(Product product) {
        productRepo.update(product);
    }

    /** Xóa sản phẩm theo mã. */
    public boolean deleteProduct(String id) {
        return productRepo.deleteById(id);
    }

    /** Tìm kiếm sản phẩm theo tên. */
    public List<Product> searchByName(String keyword) {
        return productRepo.findByName(keyword);
    }

    /**
     * Returns all products in a category.
     */
    public List<Product> getByCategory(String categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    /**
     * Returns all products from a supplier.
     */
    public List<Product> getBySupplier(String supplierId) {
        return productRepo.findBySupplierId(supplierId);
    }

    /**
     * Returns all products of a given type.
     */
    public List<Product> getByType(ProductType type) {
        return productRepo.findByType(type);
    }

    /**
     * Returns all products with stock below the threshold.
     */
    public List<Product> getLowStockProducts() {
        return productRepo.findLowStockProducts();
    }

    /**
     * Returns all perishable products that are near expiry or expired.
     */
    public List<Product> getNearExpiryProducts() {
        return productRepo.findByType(ProductType.PERISHABLE).stream()
                .filter(p -> {
                    PerishableProduct pp = (PerishableProduct) p;
                    return pp.isNearExpiry() || pp.isExpired();
                })
                .toList();
    }

    /** Đăng ký observer cho tất cả SP hiện có (gọi khi khởi tạo). */
    public void registerObserverOnAll() {
        for (Product product : productRepo.findAll()) {
            product.addObserver(this);
        }
    }

    /** Xử lý khi tồn kho thay đổi — cảnh báo nếu dưới ngưỡng. */
    @Override
    public void onStockChanged(Product product) {
        if (product.isLowStock()) {
            System.out.printf("⚠ CẢNH BÁO TỒN KHO THẤP: [%s] %s - Còn lại: %d %s%n",
                    product.getId(), product.getName(),
                    product.getStockQuantity(), product.getUnit());
        }
    }
}
