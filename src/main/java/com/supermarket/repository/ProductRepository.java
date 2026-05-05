package com.supermarket.repository;

import com.supermarket.model.Product;
import com.supermarket.model.enums.ProductType;

import java.util.List;

/**
 * Repository interface specific to Product entities.
 * Adds product-specific query methods beyond basic CRUD.
 */
public interface ProductRepository extends GenericRepository<Product> {

    /**
     * Finds products by name (partial, case-insensitive match).
     */
    List<Product> findByName(String keyword);

    /**
     * Finds all products in a given category.
     */
    List<Product> findByCategoryId(String categoryId);

    /**
     * Finds all products from a given supplier.
     */
    List<Product> findBySupplierId(String supplierId);

    /**
     * Finds all products of a specific type (Regular, Promotion, Perishable).
     */
    List<Product> findByType(ProductType type);

    /**
     * Finds all products with stock below the low-stock threshold.
     */
    List<Product> findLowStockProducts();
}
