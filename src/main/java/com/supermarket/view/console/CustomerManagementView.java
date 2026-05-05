package com.supermarket.view.console;

import com.supermarket.model.Customer;
import com.supermarket.service.CustomerService;
import com.supermarket.util.ConsoleHelper;
import com.supermarket.util.IdGenerator;

import java.util.List;

/**
 * Console view for Customer management and loyalty program.
 */
public class CustomerManagementView {

    private final CustomerService customerService;

    public CustomerManagementView(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("QUẢN LÝ KHÁCH HÀNG THÂN THIẾT");
            System.out.println("  1. Xem danh sách khách hàng");
            System.out.println("  2. Thêm khách hàng mới");
            System.out.println("  3. Cập nhật khách hàng");
            System.out.println("  4. Xóa khách hàng");
            System.out.println("  5. Tìm kiếm khách hàng");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 5);
            switch (choice) {
                case 1 -> listCustomers();
                case 2 -> addCustomer();
                case 3 -> updateCustomer();
                case 4 -> deleteCustomer();
                case 5 -> searchCustomer();
                case 0 -> { return; }
            }
        }
    }

    private void listCustomers() {
        ConsoleHelper.printHeader("DANH SÁCH KHÁCH HÀNG");
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("  Chưa có khách hàng nào.");
        } else {
            System.out.printf("  %-6s %-22s %-14s %-10s %8s %10s%n",
                    "Mã", "Họ tên", "SĐT", "Hạng", "Điểm", "Giảm giá");
            ConsoleHelper.printSeparator();
            for (Customer c : customers) {
                System.out.printf("  %-6s %-22s %-14s %-10s %8d %9.0f%%%n",
                        c.getId(), c.getName(), c.getPhone(),
                        c.getTier().getDisplayName(), c.getRewardPoints(),
                        c.getDiscountRate() * 100);
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void addCustomer() {
        ConsoleHelper.printHeader("THÊM KHÁCH HÀNG MỚI");
        String id = IdGenerator.nextId(IdGenerator.CUSTOMER_PREFIX);
        String name = ConsoleHelper.readString("  Họ tên: ");
        String phone = ConsoleHelper.readPhone("  Số điện thoại: ");
        String email = ConsoleHelper.readString("  Email: ");
        Customer customer = new Customer(id, name, phone, email);
        customerService.addCustomer(customer);
        ConsoleHelper.printSuccess("Đã thêm khách hàng: " + customer);
        ConsoleHelper.pressEnterToContinue();
    }

    private void updateCustomer() {
        String id = ConsoleHelper.readString("  Nhập mã khách hàng: ");
        try {
            Customer c = customerService.getCustomer(id);
            System.out.println("  KH hiện tại: " + c);
            c.setName(ConsoleHelper.readStringOptional("  Tên mới: ", c.getName()));
            c.setPhone(ConsoleHelper.readStringOptional("  SĐT mới: ", c.getPhone()));
            c.setEmail(ConsoleHelper.readStringOptional("  Email mới: ", c.getEmail()));
            customerService.updateCustomer(c);
            ConsoleHelper.printSuccess("Đã cập nhật KH " + id);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void deleteCustomer() {
        String id = ConsoleHelper.readString("  Nhập mã KH cần xóa: ");
        if (ConsoleHelper.confirm("  Xác nhận xóa?")) {
            if (customerService.deleteCustomer(id))
                ConsoleHelper.printSuccess("Đã xóa KH " + id);
            else
                ConsoleHelper.printError("Không tìm thấy KH " + id);
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void searchCustomer() {
        ConsoleHelper.printHeader("TÌM KIẾM KHÁCH HÀNG");
        System.out.println("  1. Tìm theo tên");
        System.out.println("  2. Tìm theo SĐT");
        int choice = ConsoleHelper.readInt("  Chọn: ", 1, 2);
        if (choice == 1) {
            String keyword = ConsoleHelper.readString("  Nhập tên: ");
            List<Customer> results = customerService.searchByName(keyword);
            if (results.isEmpty()) System.out.println("  Không tìm thấy.");
            else results.forEach(c -> System.out.println("    " + c));
        } else {
            String phone = ConsoleHelper.readString("  Nhập SĐT: ");
            Customer c = customerService.findByPhone(phone);
            if (c == null) System.out.println("  Không tìm thấy.");
            else System.out.println("    " + c);
        }
        ConsoleHelper.pressEnterToContinue();
    }
}
