package com.supermarket.repository.impl;

import com.supermarket.model.Product;
import com.supermarket.model.enums.ProductType;
import com.supermarket.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cài đặt ProductRepository lưu trong bộ nhớ (HashMap).
 */
public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> storage = new LinkedHashMap<>();

    @Override
    public void save(Product entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Product entity) {
        if (storage.containsKey(entity.getId())) {
            storage.put(entity.getId(), entity);
        }
    }

    @Override
    public boolean deleteById(String id) {
        return storage.remove(id) != null;
    }

    @Override
    public int count() {
        return storage.size();
    }

    @Override
    public List<Product> findByName(String keyword) {
        String lower = keyword.toLowerCase();
        return storage.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategoryId(String categoryId) {
        return storage.values().stream()
                .filter(p -> categoryId.equals(p.getCategoryId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findBySupplierId(String supplierId) {
        return storage.values().stream()
                .filter(p -> supplierId.equals(p.getSupplierId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByType(ProductType type) {
        return storage.values().stream()
                .filter(p -> p.getProductType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findLowStockProducts() {
        return storage.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }
}
