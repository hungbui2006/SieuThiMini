package com.supermarket.view.console;

import com.supermarket.model.*;
import com.supermarket.model.enums.ProductType;
import com.supermarket.service.*;
import com.supermarket.factory.ProductFactory;
import com.supermarket.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Console view for product CRUD operations.
 */
public class ProductManagementView {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    public ProductManagementView(ProductService productService,
                                  CategoryService categoryService,
                                  SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("QUẢN LÝ SẢN PHẨM");
            System.out.println("  1. Xem danh sách sản phẩm");
            System.out.println("  2. Thêm sản phẩm mới");
            System.out.println("  3. Cập nhật sản phẩm");
            System.out.println("  4. Xóa sản phẩm");
            System.out.println("  5. Tìm kiếm sản phẩm");
            System.out.println("  6. Xem sản phẩm tồn kho thấp");
            System.out.println("  7. Xem sản phẩm sắp hết hạn");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 7);
            switch (choice) {
                case 1 -> listProducts();
                case 2 -> addProduct();
                case 3 -> updateProduct();
                case 4 -> deleteProduct();
                case 5 -> searchProduct();
                case 6 -> showLowStock();
                case 7 -> showNearExpiry();
                case 0 -> { return; }
            }
        }
    }

    private void listProducts() {
        ConsoleHelper.printHeader("DANH SÁCH SẢN PHẨM");
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("  Chưa có sản phẩm nào.");
        } else {
            System.out.printf("  %-6s %-28s %12s %6s %-6s %-20s%n",
                    "Mã", "Tên sản phẩm", "Giá bán", "Tồn", "ĐVT", "Loại");
            ConsoleHelper.printSeparator();
            for (Product p : products) {
                System.out.printf("  %-6s %-28s %12s %6d %-6s %-20s%n",
                        p.getId(),
                        truncate(p.getName(), 28),
                        CurrencyFormatter.format(p.calculateFinalPrice()),
                        p.getStockQuantity(),
                        p.getUnit(),
                        p.getProductTypeDescription());
            }
            System.out.println("  Tổng: " + products.size() + " sản phẩm");
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void addProduct() {
        ConsoleHelper.printHeader("THÊM SẢN PHẨM MỚI");
        System.out.println("  Chọn loại sản phẩm:");
        System.out.println("  1. Sản phẩm thường");
        System.out.println("  2. Sản phẩm khuyến mãi");
        System.out.println("  3. Sản phẩm có hạn sử dụng");
        int typeChoice = ConsoleHelper.readInt("  Loại: ", 1, 3);
        ProductType type = ProductType.values()[typeChoice - 1];

        String id = IdGenerator.nextId(IdGenerator.PRODUCT_PREFIX);
        String name = ConsoleHelper.readString("  Tên sản phẩm: ");
        double price = ConsoleHelper.readPositiveDouble("  Giá bán (VND): ");
        int stock = ConsoleHelper.readInt("  Số lượng tồn kho: ", 0, 999999);
        String unit = ConsoleHelper.readString("  Đơn vị tính (cái/kg/lít/hộp/gói...): ");

        // Select category
        System.out.println("\n  Danh mục:");
        categoryService.getAllCategories().forEach(c -> System.out.println("    " + c));
        String catId = ConsoleHelper.readString("  Mã danh mục: ");

        // Select supplier
        System.out.println("\n  Nhà cung cấp:");
        supplierService.getAllSuppliers().forEach(s -> System.out.println("    " + s));
        String supId = ConsoleHelper.readString("  Mã NCC: ");

        Product product;
        switch (type) {
            case PROMOTION -> {
                double discount = ConsoleHelper.readPositiveDouble("  Tỷ lệ giảm giá (ví dụ 0.15 = 15%): ");
                String startStr = ConsoleHelper.readString("  Ngày bắt đầu KM (dd/MM/yyyy): ");
                String endStr = ConsoleHelper.readString("  Ngày kết thúc KM (dd/MM/yyyy): ");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                product = ProductFactory.createPromotionProduct(id, name, price, stock, unit, catId, supId,
                        discount, LocalDate.parse(startStr, fmt), LocalDate.parse(endStr, fmt));
            }
            case PERISHABLE -> {
                String mfgStr = ConsoleHelper.readString("  Ngày sản xuất (dd/MM/yyyy): ");
                String expStr = ConsoleHelper.readString("  Ngày hết hạn (dd/MM/yyyy): ");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                product = ProductFactory.createPerishableProduct(id, name, price, stock, unit, catId, supId,
                        LocalDate.parse(mfgStr, fmt), LocalDate.parse(expStr, fmt));
            }
            default -> product = ProductFactory.createProduct(type, id, name, price, stock, unit, catId, supId);
        }

        productService.addProduct(product);
        ConsoleHelper.printSuccess("Đã thêm sản phẩm: " + product);
        ConsoleHelper.pressEnterToContinue();
    }

    private void updateProduct() {
        ConsoleHelper.printHeader("CẬP NHẬT SẢN PHẨM");
        String id = ConsoleHelper.readString("  Nhập mã sản phẩm: ");
        try {
            Product product = productService.getProduct(id);
            System.out.println("  Sản phẩm hiện tại: " + product);
            System.out.println("  (Nhấn Enter để giữ nguyên giá trị cũ)");

            String name = ConsoleHelper.readStringOptional("  Tên mới: ", product.getName());
            product.setName(name);

            String priceStr = ConsoleHelper.readStringOptional(
                    "  Giá mới (" + CurrencyFormatter.format(product.getPrice()) + "): ", "");
            if (!priceStr.isEmpty()) product.setPrice(Double.parseDouble(priceStr));

            String unitStr = ConsoleHelper.readStringOptional("  ĐVT mới (" + product.getUnit() + "): ", product.getUnit());
            product.setUnit(unitStr);

            productService.updateProduct(product);
            ConsoleHelper.printSuccess("Đã cập nhật sản phẩm: " + product);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void deleteProduct() {
        ConsoleHelper.printHeader("XÓA SẢN PHẨM");
        String id = ConsoleHelper.readString("  Nhập mã sản phẩm cần xóa: ");
        try {
            Product product = productService.getProduct(id);
            System.out.println("  Sản phẩm: " + product);
            if (ConsoleHelper.confirm("  Xác nhận xóa?")) {
                productService.deleteProduct(id);
                ConsoleHelper.printSuccess("Đã xóa sản phẩm " + id);
            } else {
                System.out.println("  Đã hủy.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void searchProduct() {
        ConsoleHelper.printHeader("TÌM KIẾM SẢN PHẨM");
        String keyword = ConsoleHelper.readString("  Nhập từ khóa tìm kiếm: ");
        List<Product> results = productService.searchByName(keyword);
        if (results.isEmpty()) {
            System.out.println("  Không tìm thấy sản phẩm nào.");
        } else {
            System.out.println("  Kết quả (" + results.size() + " sản phẩm):");
            for (Product p : results) {
                System.out.println("    " + p + " | " + p.getProductTypeDescription());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void showLowStock() {
        ConsoleHelper.printHeader("SẢN PHẨM TỒN KHO THẤP (< 10)");
        List<Product> lowStock = productService.getLowStockProducts();
        if (lowStock.isEmpty()) {
            System.out.println("  Tất cả sản phẩm đều đủ tồn kho.");
        } else {
            for (Product p : lowStock) {
                System.out.printf("  ⚠ [%s] %s - Tồn kho: %d %s%n",
                        p.getId(), p.getName(), p.getStockQuantity(), p.getUnit());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void showNearExpiry() {
        ConsoleHelper.printHeader("SẢN PHẨM SẮP HẾT HẠN / ĐÃ HẾT HẠN");
        List<Product> nearExpiry = productService.getNearExpiryProducts();
        if (nearExpiry.isEmpty()) {
            System.out.println("  Không có sản phẩm nào sắp hết hạn.");
        } else {
            for (Product p : nearExpiry) {
                PerishableProduct pp = (PerishableProduct) p;
                System.out.printf("  %s [%s] %s - HSD: %s%n",
                        pp.isExpired() ? "✗" : "⚠",
                        pp.getId(), pp.getName(), pp.getExpiryDate());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private String truncate(String text, int maxLen) {
        return text.length() > maxLen ? text.substring(0, maxLen - 3) + "..." : text;
    }
}
