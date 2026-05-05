package com.supermarket.view.swing;

import com.supermarket.datastore.DataSeeder;
import com.supermarket.model.User;
import com.supermarket.service.*;

import javax.swing.*;

/**
 * Entry point for the Java Swing GUI application.
 */
public class SwingApp {

    public static void main(String[] args) {
        // Set look and feel to FlatLaf-style using Nimbus for a modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Custom Phenikaa color scheme
        UIManager.put("nimbusBase", new java.awt.Color(0, 51, 153));
        UIManager.put("nimbusBlueGrey", new java.awt.Color(0, 35, 102));
        UIManager.put("control", new java.awt.Color(240, 243, 248));

        // Global exception handler for EDT — shows error dialog instead of silent crash
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi không xác định:\n" + e.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        });

        SwingUtilities.invokeLater(() -> {
            try {
                // Seed initial data
                DataSeeder.seed();

                // Init all services
                ProductService productService = new ProductService();
                CategoryService categoryService = new CategoryService();
                SupplierService supplierService = new SupplierService();
                EmployeeService employeeService = new EmployeeService();
                CustomerService customerService = new CustomerService();
                OrderService orderService = new OrderService(productService, customerService);
                GoodsImportService importService = new GoodsImportService(productService);
                ReportService reportService = new ReportService(orderService, productService, employeeService);
                AuthService authService = new AuthService();

                productService.registerObserverOnAll();

                // Show login
                LoginDialog login = new LoginDialog(null, authService);
                login.setVisible(true);

                User user = login.getAuthenticatedUser();
                if (user == null) {
                    System.exit(0);
                    return;
                }

                // Show main dashboard
                DashboardFrame dashboard = new DashboardFrame(
                        user, authService, productService, categoryService,
                        supplierService, employeeService, customerService,
                        orderService, importService, reportService);
                dashboard.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Không thể khởi động ứng dụng:\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                        "Lỗi khởi động", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
