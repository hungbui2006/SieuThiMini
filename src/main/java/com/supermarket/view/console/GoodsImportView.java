package com.supermarket.view.console;

import com.supermarket.model.*;
import com.supermarket.service.*;
import com.supermarket.util.*;

import java.util.List;

/**
 * Console view for goods import (nhập hàng).
 */
public class GoodsImportView {

    private final GoodsImportService importService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public GoodsImportView(GoodsImportService importService, ProductService productService,
                           SupplierService supplierService) {
        this.importService = importService;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    public void show(User currentUser) {
        while (true) {
            ConsoleHelper.printHeader("NHẬP HÀNG TỪ NHÀ CUNG CẤP");
            System.out.println("  1. Tạo phiếu nhập hàng");
            System.out.println("  2. Xem lịch sử nhập hàng");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn: ", 0, 2);
            switch (choice) {
                case 1 -> createImport(currentUser);
                case 2 -> listImports();
                case 0 -> { return; }
            }
        }
    }

    private void createImport(User currentUser) {
        ConsoleHelper.printHeader("TẠO PHIẾU NHẬP HÀNG");

        // Select supplier
        System.out.println("  Danh sách nhà cung cấp:");
        supplierService.getAllSuppliers().forEach(s -> System.out.println("    " + s));
        String supplierId = ConsoleHelper.readString("  Nhập mã NCC: ");
        Supplier supplier;
        try {
            supplier = supplierService.getSupplier(supplierId);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
            return;
        }

        GoodsImport goodsImport = importService.createImport(
                supplier.getId(), supplier.getName(),
                currentUser.getId(), currentUser.getFullName());

        System.out.println("  Mã phiếu nhập: " + goodsImport.getId());
        System.out.println("  NCC: " + supplier.getName());
        ConsoleHelper.printSeparator();

        // Add items
        while (true) {
            String productId = ConsoleHelper.readString("  Mã SP cần nhập (0 để kết thúc): ");
            if (productId.equals("0")) break;

            try {
                Product product = productService.getProduct(productId);
                System.out.println("    SP: " + product.getName() + " (Tồn kho: " + product.getStockQuantity() + ")");
                int qty = ConsoleHelper.readInt("    Số lượng nhập: ", 1, 999999);
                double importPrice = ConsoleHelper.readPositiveDouble("    Giá nhập (VND): ");
                importService.addImportItem(goodsImport, productId, qty, importPrice);
                ConsoleHelper.printSuccess("Đã thêm: " + product.getName() + " x" + qty);
            } catch (Exception e) {
                ConsoleHelper.printError(e.getMessage());
            }
        }

        if (!goodsImport.getItems().isEmpty()) {
            goodsImport.setNotes(ConsoleHelper.readStringOptional("  Ghi chú: ", ""));
            importService.saveImport(goodsImport);
            ConsoleHelper.printSuccess("Đã lưu phiếu nhập " + goodsImport.getId() +
                    " - Tổng: " + CurrencyFormatter.format(goodsImport.getTotalCost()));
        } else {
            System.out.println("  Phiếu nhập trống, đã hủy.");
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void listImports() {
        ConsoleHelper.printHeader("LỊCH SỬ NHẬP HÀNG");
        List<GoodsImport> imports = importService.getAllImports();
        if (imports.isEmpty()) {
            System.out.println("  Chưa có phiếu nhập nào.");
        } else {
            for (GoodsImport gi : imports) {
                System.out.printf("  [%s] %s - NCC: %s - %d SP - Tổng: %s%n",
                        gi.getId(),
                        gi.getImportDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        gi.getSupplierName(),
                        gi.getItems().size(),
                        CurrencyFormatter.format(gi.getTotalCost()));
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }
}
