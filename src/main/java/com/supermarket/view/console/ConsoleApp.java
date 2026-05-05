package com.supermarket.view.console;

import com.supermarket.datastore.DataSeeder;
import com.supermarket.model.User;
import com.supermarket.model.enums.UserRole;
import com.supermarket.service.*;
import com.supermarket.util.ConsoleHelper;

/**
 * Main console application entry point.
 * Orchestrates login flow and routes to the correct menu based on user role.
 */
public class ConsoleApp {

    private final AuthService authService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final GoodsImportService importService;
    private final ReportService reportService;

    public ConsoleApp() {
        // Initialize services
        this.authService = new AuthService();
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();
        this.employeeService = new EmployeeService();
        this.customerService = new CustomerService();
        this.orderService = new OrderService(productService, customerService);
        this.importService = new GoodsImportService(productService);
        this.reportService = new ReportService(orderService, productService, employeeService);
    }

    /**
     * Starts the console application main loop.
     */
    public void run() {
        // Seed initial data
        DataSeeder.seed();

        // Register stock observer on seeded products
        productService.registerObserverOnAll();

        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║        HỆ THỐNG QUẢN LÝ SIÊU THỊ BÁN HÀNG              ║");
        System.out.println("║        Supermarket Sales Management System                ║");
        System.out.println("║                                                           ║");
        System.out.println("║        Phiên bản: 1.0                                     ║");
        System.out.println("║        Tác giả: OOP Course Project                        ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        // Main application loop — keeps running until user exits
        while (true) {
            LoginView loginView = new LoginView(authService);
            User user = loginView.showLogin();

            if (user == null) {
                System.out.println("\n  Cảm ơn bạn đã sử dụng hệ thống. Tạm biệt!");
                break;
            }

            // Route to the correct menu based on role
            if (user.getRole() == UserRole.ADMIN) {
                AdminMenuView adminMenu = new AdminMenuView(
                        user, productService, categoryService, supplierService,
                        employeeService, customerService, orderService,
                        importService, reportService);
                adminMenu.show();
            } else {
                CashierMenuView cashierMenu = new CashierMenuView(
                        user, productService, customerService,
                        orderService, reportService);
                cashierMenu.show();
            }

            // After logout, loop back to login
            authService.logout();
        }
    }
}
