package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.model.Supplier;
import com.supermarket.repository.SupplierRepository;

import java.util.List;

/**
 * Service responsible for Supplier CRUD operations.
 */
public class SupplierService {

    private final SupplierRepository supplierRepo;

    public SupplierService() {
        this.supplierRepo = DataStore.getInstance().getSupplierRepository();
    }

    public void addSupplier(Supplier supplier) {
        supplierRepo.save(supplier);
    }

    public Supplier getSupplier(String id) {
        return supplierRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy nhà cung cấp với mã: " + id));
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }

    public void updateSupplier(Supplier supplier) {
        supplierRepo.update(supplier);
    }

    public boolean deleteSupplier(String id) {
        return supplierRepo.deleteById(id);
    }
}
