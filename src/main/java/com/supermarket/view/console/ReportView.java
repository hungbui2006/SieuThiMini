package com.supermarket.view.console;

import com.supermarket.model.*;
import com.supermarket.service.*;
import com.supermarket.util.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Console view for generating reports and statistics.
 */
public class ReportView {

    private final ReportService reportService;
    private final OrderService orderService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportView(ReportService reportService, OrderService orderService) {
        this.reportService = reportService;
        this.orderService = orderService;
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("BÁO CÁO & THỐNG KÊ");
            System.out.println("  1. Doanh thu theo ngày");
            System.out.println("  2. Doanh thu theo khoảng thời gian");
            System.out.println("  3. Sản phẩm bán chạy nhất");
            System.out.println("  4. Sản phẩm bán chậm nhất");
            System.out.println("  5. Doanh số theo nhân viên");
            System.out.println("  6. Báo cáo tồn kho");
            System.out.println("  7. Sản phẩm sắp hết hạn");
            System.out.println("  8. Sản phẩm tồn kho thấp");
            System.out.println("  9. Xem lịch sử hóa đơn");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn: ", 0, 9);
            switch (choice) {
                case 1 -> dailyRevenue();
                case 2 -> rangeRevenue();
                case 3 -> topSelling();
                case 4 -> slowSelling();
                case 5 -> salesByEmployee();
                case 6 -> inventoryReport();
                case 7 -> nearExpiryReport();
                case 8 -> lowStockReport();
                case 9 -> orderHistory();
                case 0 -> { return; }
            }
        }
    }

    private void dailyRevenue() {
        ConsoleHelper.printHeader("DOANH THU THEO NGÀY");
        String dateStr = ConsoleHelper.readString("  Nhập ngày (dd/MM/yyyy) [Enter = hôm nay]: ");
        LocalDate date;
        try {
            date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr, DATE_FMT);
        } catch (Exception e) {
            date = LocalDate.now();
        }
        double revenue = reportService.getRevenueByDate(date);
        List<Order> orders = orderService.getOrdersByDate(date);
        System.out.printf("  Ngày: %s%n", date.format(DATE_FMT));
        System.out.printf("  Số hóa đơn: %d%n", orders.stream().filter(Order::isCompleted).count());
        System.out.printf("  Tổng doanh thu: %s%n", CurrencyFormatter.format(revenue));
        offerExport("doanh_thu_" + date + ".csv", generateRevenueCSV(date, revenue));
        ConsoleHelper.pressEnterToContinue();
    }

    private void rangeRevenue() {
        ConsoleHelper.printHeader("DOANH THU THEO KHOẢNG THỜI GIAN");
        String fromStr = ConsoleHelper.readString("  Từ ngày (dd/MM/yyyy): ");
        String toStr = ConsoleHelper.readString("  Đến ngày (dd/MM/yyyy): ");
        try {
            LocalDate from = LocalDate.parse(fromStr, DATE_FMT);
            LocalDate to = LocalDate.parse(toStr, DATE_FMT);
            double revenue = reportService.getRevenueByDateRange(from, to);
            System.out.printf("  Từ %s đến %s%n", from.format(DATE_FMT), to.format(DATE_FMT));
            System.out.printf("  Tổng doanh thu: %s%n", CurrencyFormatter.format(revenue));
        } catch (Exception e) {
            ConsoleHelper.printError("Định dạng ngày không hợp lệ!");
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void topSelling() {
        ConsoleHelper.printHeader("TOP SẢN PHẨM BÁN CHẠY");
        int limit = ConsoleHelper.readInt("  Số lượng top (mặc định 10): ", 1, 50);
        var top = reportService.getTopSellingProducts(limit);
        if (top.isEmpty()) {
            System.out.println("  Chưa có dữ liệu bán hàng.");
        } else {
            System.out.printf("  %-5s %-40s %10s%n", "STT", "Sản phẩm", "Đã bán");
            ConsoleHelper.printSeparator();
            int i = 1;
            for (var entry : top) {
                System.out.printf("  %-5d %-40s %10d%n", i++, entry.getKey(), entry.getValue());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void slowSelling() {
        ConsoleHelper.printHeader("SẢN PHẨM BÁN CHẬM");
        int limit = ConsoleHelper.readInt("  Số lượng (mặc định 10): ", 1, 50);
        var slow = reportService.getSlowSellingProducts(limit);
        if (slow.isEmpty()) {
            System.out.println("  Chưa có dữ liệu.");
        } else {
            System.out.printf("  %-5s %-40s %10s%n", "STT", "Sản phẩm", "Đã bán");
            ConsoleHelper.printSeparator();
            int i = 1;
            for (var entry : slow) {
                System.out.printf("  %-5d %-40s %10d%n", i++, entry.getKey(), entry.getValue());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void salesByEmployee() {
        ConsoleHelper.printHeader("DOANH SỐ THEO NHÂN VIÊN");
        Map<String, Double> sales = reportService.getSalesByEmployee();
        if (sales.isEmpty()) {
            System.out.println("  Chưa có dữ liệu bán hàng.");
        } else {
            System.out.printf("  %-30s %20s%n", "Nhân viên", "Doanh số");
            ConsoleHelper.printSeparator();
            for (var entry : sales.entrySet()) {
                System.out.printf("  %-30s %20s%n", entry.getKey(), CurrencyFormatter.format(entry.getValue()));
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void inventoryReport() {
        ConsoleHelper.printHeader("BÁO CÁO TỒN KHO");
        List<Product> products = reportService.getInventoryReport();
        System.out.printf("  %-6s %-28s %10s %6s %-6s%n", "Mã", "Tên", "Giá", "Tồn", "ĐVT");
        ConsoleHelper.printSeparator();
        for (Product p : products) {
            String flag = p.isLowStock() ? " ⚠" : "";
            System.out.printf("  %-6s %-28s %10s %6d %-6s%s%n",
                    p.getId(), truncate(p.getName(), 28),
                    CurrencyFormatter.format(p.getPrice()),
                    p.getStockQuantity(), p.getUnit(), flag);
        }
        System.out.println("  Tổng: " + products.size() + " sản phẩm");
        offerExport("tonkho.csv", generateInventoryCSV(products));
        ConsoleHelper.pressEnterToContinue();
    }

    private void nearExpiryReport() {
        ConsoleHelper.printHeader("SẢN PHẨM SẮP HẾT HẠN / ĐÃ HẾT HẠN");
        List<Product> products = reportService.getNearExpiryReport();
        if (products.isEmpty()) {
            System.out.println("  Không có sản phẩm nào sắp hết hạn.");
        } else {
            for (Product p : products) {
                PerishableProduct pp = (PerishableProduct) p;
                System.out.printf("  %s [%s] %s - HSD: %s - Tồn: %d%n",
                        pp.isExpired() ? "✗" : "⚠",
                        pp.getId(), pp.getName(), pp.getExpiryDate(), pp.getStockQuantity());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void lowStockReport() {
        ConsoleHelper.printHeader("SẢN PHẨM TỒN KHO THẤP (< 10)");
        List<Product> products = reportService.getLowStockReport();
        if (products.isEmpty()) {
            System.out.println("  Tất cả sản phẩm đều đủ tồn kho.");
        } else {
            for (Product p : products) {
                System.out.printf("  ⚠ [%s] %s - Tồn: %d %s%n",
                        p.getId(), p.getName(), p.getStockQuantity(), p.getUnit());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void orderHistory() {
        ConsoleHelper.printHeader("LỊCH SỬ HÓA ĐƠN");
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("  Chưa có hóa đơn nào.");
        } else {
            DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Order o : orders) {
                System.out.printf("  [%s] %s - NV: %s - KH: %s - Tổng: %s%n",
                        o.getId(),
                        o.getOrderDate().format(dtFmt),
                        o.getEmployeeName(),
                        o.getCustomerName() != null ? o.getCustomerName() : "Khách vãng lai",
                        CurrencyFormatter.format(o.getFinalAmount()));
            }

            // Option to reprint
            String orderId = ConsoleHelper.readStringOptional("  Nhập mã HĐ để xem chi tiết (Enter để bỏ qua): ", "");
            if (!orderId.isEmpty()) {
                try {
                    Order order = orderService.getOrder(orderId);
                    InvoicePrinter.printToConsole(order);
                } catch (Exception e) {
                    ConsoleHelper.printError(e.getMessage());
                }
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    // --- CSV Export Helpers ---

    private void offerExport(String defaultName, String csvContent) {
        if (ConsoleHelper.confirm("  Xuất báo cáo ra file CSV?")) {
            String fileName = ConsoleHelper.readStringOptional("  Tên file (" + defaultName + "): ", defaultName);
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
                pw.print(csvContent);
                ConsoleHelper.printSuccess("Đã xuất ra file: " + fileName);
            } catch (IOException e) {
                ConsoleHelper.printError("Lỗi xuất file: " + e.getMessage());
            }
        }
    }

    private String generateRevenueCSV(LocalDate date, double revenue) {
        return String.format("Ngày,Doanh thu%n%s,%s%n", date.format(DATE_FMT), String.format("%.0f", revenue));
    }

    private String generateInventoryCSV(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mã SP,Tên SP,Giá,Tồn kho,ĐVT\n");
        for (Product p : products) {
            sb.append(String.format("%s,\"%s\",%.0f,%d,%s%n",
                    p.getId(), p.getName(), p.getPrice(), p.getStockQuantity(), p.getUnit()));
        }
        return sb.toString();
    }

    private String truncate(String text, int maxLen) {
        return text.length() > maxLen ? text.substring(0, maxLen - 3) + "..." : text;
    }
}
