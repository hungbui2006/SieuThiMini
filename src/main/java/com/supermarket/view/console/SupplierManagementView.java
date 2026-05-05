package com.supermarket.view.console;

import com.supermarket.model.Supplier;
import com.supermarket.service.SupplierService;
import com.supermarket.util.ConsoleHelper;
import com.supermarket.util.IdGenerator;

import java.util.List;

/**
 * Console view for Supplier management.
 */
public class SupplierManagementView {

    private final SupplierService supplierService;

    public SupplierManagementView(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("QUẢN LÝ NHÀ CUNG CẤP");
            System.out.println("  1. Xem danh sách NCC");
            System.out.println("  2. Thêm NCC mới");
            System.out.println("  3. Cập nhật NCC");
            System.out.println("  4. Xóa NCC");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 4);
            switch (choice) {
                case 1 -> listSuppliers();
                case 2 -> addSupplier();
                case 3 -> updateSupplier();
                case 4 -> deleteSupplier();
                case 0 -> { return; }
            }
        }
    }

    private void listSuppliers() {
        ConsoleHelper.printHeader("DANH SÁCH NHÀ CUNG CẤP");
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        if (suppliers.isEmpty()) {
            System.out.println("  Chưa có nhà cung cấp nào.");
        } else {
            for (Supplier s : suppliers) {
                System.out.printf("  [%s] %s%n", s.getId(), s.getName());
                System.out.printf("        SĐT: %s | Địa chỉ: %s%n", s.getContactPhone(), s.getAddress());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void addSupplier() {
        ConsoleHelper.printHeader("THÊM NHÀ CUNG CẤP MỚI");
        String id = IdGenerator.nextId(IdGenerator.SUPPLIER_PREFIX);
        String name = ConsoleHelper.readString("  Tên NCC: ");
        String phone = ConsoleHelper.readString("  SĐT: ");
        String address = ConsoleHelper.readString("  Địa chỉ: ");
        String email = ConsoleHelper.readString("  Email: ");
        supplierService.addSupplier(new Supplier(id, name, phone, address, email));
        ConsoleHelper.printSuccess("Đã thêm NCC: [" + id + "] " + name);
        ConsoleHelper.pressEnterToContinue();
    }

    private void updateSupplier() {
        String id = ConsoleHelper.readString("  Nhập mã NCC: ");
        try {
            Supplier s = supplierService.getSupplier(id);
            s.setName(ConsoleHelper.readStringOptional("  Tên mới: ", s.getName()));
            s.setContactPhone(ConsoleHelper.readStringOptional("  SĐT mới: ", s.getContactPhone()));
            s.setAddress(ConsoleHelper.readStringOptional("  Địa chỉ mới: ", s.getAddress()));
            s.setEmail(ConsoleHelper.readStringOptional("  Email mới: ", s.getEmail()));
            supplierService.updateSupplier(s);
            ConsoleHelper.printSuccess("Đã cập nhật NCC " + id);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void deleteSupplier() {
        String id = ConsoleHelper.readString("  Nhập mã NCC cần xóa: ");
        if (ConsoleHelper.confirm("  Xác nhận xóa?")) {
            if (supplierService.deleteSupplier(id))
                ConsoleHelper.printSuccess("Đã xóa NCC " + id);
            else
                ConsoleHelper.printError("Không tìm thấy NCC " + id);
        }
        ConsoleHelper.pressEnterToContinue();
    }
}
