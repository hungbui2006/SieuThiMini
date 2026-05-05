package com.supermarket.datastore;

import com.supermarket.repository.*;
import com.supermarket.repository.impl.*;

/**
 * Lưu trữ dữ liệu trung tâm (Singleton) — chứa tất cả các repository.
 */
public class DataStore {

    // Đảm bảo chỉ có 1 instance duy nhất
    private static volatile DataStore instance;

    // Các repository
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final GoodsImportRepository goodsImportRepository;

    /** Constructor private — không cho tạo từ bên ngoài. */
    private DataStore() {
        this.productRepository = new InMemoryProductRepository();
        this.userRepository = new InMemoryUserRepository();
        this.customerRepository = new InMemoryCustomerRepository();
        this.orderRepository = new InMemoryOrderRepository();
        this.categoryRepository = new InMemoryCategoryRepository();
        this.supplierRepository = new InMemorySupplierRepository();
        this.goodsImportRepository = new InMemoryGoodsImportRepository();
    }

    /** Lấy instance duy nhất (thread-safe). */
    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (DataStore.class) {
                if (instance == null) {
                    instance = new DataStore();
                }
            }
        }
        return instance;
    }

    // --- Repository Accessors ---

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public SupplierRepository getSupplierRepository() {
        return supplierRepository;
    }

    public GoodsImportRepository getGoodsImportRepository() {
        return goodsImportRepository;
    }
}
