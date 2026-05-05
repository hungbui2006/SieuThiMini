package com.supermarket.service;

import com.supermarket.model.*;
import com.supermarket.util.CurrencyFormatter;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating business reports and statistics.
 */
public class ReportService {

    private final OrderService orderService;
    private final ProductService productService;
    private final EmployeeService employeeService;

    public ReportService(OrderService orderService, ProductService productService,
                         EmployeeService employeeService) {
        this.orderService = orderService;
        this.productService = productService;
        this.employeeService = employeeService;
    }

    /** Revenue for a specific date. */
    public double getRevenueByDate(LocalDate date) {
        return orderService.getOrdersByDate(date).stream()
                .filter(Order::isCompleted)
                .mapToDouble(Order::getFinalAmount)
                .sum();
    }

    /** Revenue for a date range. */
    public double getRevenueByDateRange(LocalDate from, LocalDate to) {
        return orderService.getOrdersByDateRange(from, to).stream()
                .filter(Order::isCompleted)
                .mapToDouble(Order::getFinalAmount)
                .sum();
    }

    /** Top-selling products by total quantity sold. */
    public List<Map.Entry<String, Integer>> getTopSellingProducts(int limit) {
        Map<String, Integer> salesMap = new LinkedHashMap<>();
        for (Order order : orderService.getAllOrders()) {
            if (!order.isCompleted()) continue;
            for (OrderItem item : order.getItems()) {
                salesMap.merge(item.getProductId() + " - " + item.getProductName(),
                        item.getQuantity(), Integer::sum);
            }
        }
        return salesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /** Slow-selling products (least sold). */
    public List<Map.Entry<String, Integer>> getSlowSellingProducts(int limit) {
        Map<String, Integer> salesMap = new LinkedHashMap<>();
        // Initialize all products with 0 sales
        for (Product p : productService.getAllProducts()) {
            salesMap.put(p.getId() + " - " + p.getName(), 0);
        }
        for (Order order : orderService.getAllOrders()) {
            if (!order.isCompleted()) continue;
            for (OrderItem item : order.getItems()) {
                salesMap.merge(item.getProductId() + " - " + item.getProductName(),
                        item.getQuantity(), Integer::sum);
            }
        }
        return salesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /** Sales amount by employee. */
    public Map<String, Double> getSalesByEmployee() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Order order : orderService.getAllOrders()) {
            if (!order.isCompleted()) continue;
            String key = order.getEmployeeId() + " - " + order.getEmployeeName();
            result.merge(key, order.getFinalAmount(), Double::sum);
        }
        return result;
    }

    /** Current inventory status for all products. */
    public List<Product> getInventoryReport() {
        return productService.getAllProducts();
    }

    /** Products near expiry or expired. */
    public List<Product> getNearExpiryReport() {
        return productService.getNearExpiryProducts();
    }

    /** Low stock products. */
    public List<Product> getLowStockReport() {
        return productService.getLowStockProducts();
    }

    /** Total number of orders. */
    public int getTotalOrderCount() {
        return (int) orderService.getAllOrders().stream().filter(Order::isCompleted).count();
    }

    /** Total revenue of all time. */
    public double getTotalRevenue() {
        return orderService.getAllOrders().stream()
                .filter(Order::isCompleted)
                .mapToDouble(Order::getFinalAmount)
                .sum();
    }
}
