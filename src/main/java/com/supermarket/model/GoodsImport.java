package com.supermarket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a goods import receipt (phiếu nhập hàng).
 * Records incoming stock from suppliers.
 */
public class GoodsImport {
    private String id;
    private LocalDateTime importDate;
    private String supplierId;
    private String supplierName;
    private String employeeId;      // Employee who processed the import
    private String employeeName;
    private List<ImportItem> items;
    private double totalCost;
    private String notes;

    public GoodsImport() {
        this.items = new ArrayList<>();
        this.importDate = LocalDateTime.now();
    }

    public GoodsImport(String id, String supplierId, String supplierName,
                       String employeeId, String employeeName) {
        this();
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    /**
     * Adds an import item and recalculates the total cost.
     */
    public void addItem(ImportItem item) {
        items.add(item);
        recalculateTotal();
    }

    /**
     * Recalculates the total cost of the import.
     */
    public void recalculateTotal() {
        this.totalCost = 0;
        for (ImportItem item : items) {
            this.totalCost += item.getSubTotal();
        }
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public List<ImportItem> getItems() {
        return items;
    }

    public void setItems(List<ImportItem> items) {
        this.items = items;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
