package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.model.*;
import com.supermarket.repository.GoodsImportRepository;
import com.supermarket.util.IdGenerator;

/**
 * Service responsible for goods import (nhập hàng) operations.
 */
public class GoodsImportService {

    private final GoodsImportRepository importRepo;
    private final ProductService productService;

    public GoodsImportService(ProductService productService) {
        this.importRepo = DataStore.getInstance().getGoodsImportRepository();
        this.productService = productService;
    }

    public GoodsImport createImport(String supplierId, String supplierName,
                                     String employeeId, String employeeName) {
        String importId = IdGenerator.nextId(IdGenerator.IMPORT_PREFIX);
        return new GoodsImport(importId, supplierId, supplierName, employeeId, employeeName);
    }

    public void addImportItem(GoodsImport goodsImport, String productId, int quantity, double importPrice) {
        Product product = productService.getProduct(productId);
        ImportItem item = new ImportItem(productId, product.getName(), quantity, importPrice);
        goodsImport.addItem(item);
        product.updateStock(quantity);
        product.setImportDate(java.time.LocalDate.now()); // Cập nhật ngày nhập hàng
        productService.updateProduct(product);
    }

    public void saveImport(GoodsImport goodsImport) {
        importRepo.save(goodsImport);
    }

    public java.util.List<GoodsImport> getAllImports() {
        return importRepo.findAll();
    }
}
