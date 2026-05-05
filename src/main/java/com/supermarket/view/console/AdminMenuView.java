package com.supermarket.view.console;

import com.supermarket.model.Admin;
import com.supermarket.model.User;
import com.supermarket.service.*;
import com.supermarket.util.ConsoleHelper;

/**
 * Admin menu — full access to all management features.
 * Menu quản trị viên.
 */
public class AdminMenuView {

    private final ProductManagementView productView;
    private final CategoryManagementView categoryView;
    private final SupplierManagementView supplierView;
    private final EmployeeManagementView employeeView;
    private final CustomerManagementView customerView;
    private final GoodsImportView importView;
    private final ReportView reportView;
    private final POSView posView;
    private final User currentUser;

    public AdminMenuView(User currentUser,
                         ProductService productService,
                         CategoryService categoryService,
                         SupplierService supplierService,
                         EmployeeService employeeService,
                         CustomerService customerService,
                         OrderService orderService,
                         GoodsImportService importService,
                         ReportService reportService) {
        this.currentUser = currentUser;
        this.productView = new ProductManagementView(productService, categoryService, supplierService);
        this.categoryView = new CategoryManagementView(categoryService);
        this.supplierView = new SupplierManagementView(supplierService);
        this.employeeView = new EmployeeManagementView(employeeService);
        this.customerView = new CustomerManagementView(customerService);
        this.importView = new GoodsImportView(importService, productService, supplierService);
        this.reportView = new ReportView(reportService, orderService);
        this.posView = new POSView(orderService, productService, customerService);
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("MENU QUẢN TRỊ VIÊN");
            System.out.println("  Xin chào, " + currentUser.getFullName() + " (Admin)");
            ConsoleHelper.printSeparator();
            System.out.println("  1.  Quản lý Sản phẩm");
            System.out.println("  2.  Quản lý Danh mục");
            System.out.println("  3.  Quản lý Nhà cung cấp");
            System.out.println("  4.  Quản lý Nhân viên");
            System.out.println("  5.  Quản lý Khách hàng");
            System.out.println("  6.  Nhập hàng");
            System.out.println("  7.  Tạo Hóa đơn (POS)");
            System.out.println("  8.  Báo cáo & Thống kê");
            System.out.println("  0.  Đăng xuất");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 8);
            switch (choice) {
                case 1 -> productView.show();
                case 2 -> categoryView.show();
                case 3 -> supplierView.show();
                case 4 -> employeeView.show();
                case 5 -> customerView.show();
                case 6 -> importView.show(currentUser);
                case 7 -> {
                    // Admin can also use POS — cast to Employee-like behavior
                    // For simplicity, admin creates orders under their own name
                    com.supermarket.model.Employee tempEmp = new com.supermarket.model.Employee(
                            currentUser.getId(), currentUser.getUsername(), "",
                            currentUser.getFullName(), currentUser.getPhone(), 0, 0);
                    posView.show(tempEmp);
                }
                case 8 -> reportView.show();
                case 0 -> {
                    System.out.println("  Đăng xuất thành công!");
                    return;
                }
            }
        }
    }
}
