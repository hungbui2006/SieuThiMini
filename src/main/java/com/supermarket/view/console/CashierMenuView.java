package com.supermarket.view.console;

import com.supermarket.model.Employee;
import com.supermarket.model.User;
import com.supermarket.service.*;
import com.supermarket.util.ConsoleHelper;

/**
 * Cashier menu — limited access for point-of-sale operations.
 * Menu nhân viên bán hàng.
 */
public class CashierMenuView {

    private final POSView posView;
    private final CustomerManagementView customerView;
    private final ReportView reportView;
    private final Employee employee;

    public CashierMenuView(User currentUser,
                           ProductService productService,
                           CustomerService customerService,
                           OrderService orderService,
                           ReportService reportService) {
        this.employee = (Employee) currentUser;
        this.posView = new POSView(orderService, productService, customerService);
        this.customerView = new CustomerManagementView(customerService);
        this.reportView = new ReportView(reportService, orderService);
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("MENU NHÂN VIÊN BÁN HÀNG");
            System.out.println("  Xin chào, " + employee.getFullName() + " (Thu ngân)");
            ConsoleHelper.printSeparator();
            System.out.println("  1. Tạo Hóa đơn (POS)");
            System.out.println("  2. Quản lý Khách hàng");
            System.out.println("  3. Xem lịch sử hóa đơn");
            System.out.println("  0. Đăng xuất");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 3);
            switch (choice) {
                case 1 -> posView.show(employee);
                case 2 -> customerView.show();
                case 3 -> reportView.show();
                case 0 -> {
                    System.out.println("  Đăng xuất thành công!");
                    return;
                }
            }
        }
    }
}
