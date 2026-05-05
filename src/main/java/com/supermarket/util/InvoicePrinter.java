package com.supermarket.util;

import com.supermarket.model.Order;
import com.supermarket.model.OrderItem;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for printing invoices (hóa đơn) in Vietnamese format.
 * Supports both console output and file export.
 */
public final class InvoicePrinter {

    private static final String STORE_NAME = "SIÊU THỊ MINI VIỆT NAM";
    private static final String STORE_ADDRESS = "123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh";
    private static final String STORE_PHONE = "Hotline: 1900-1234";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final int WIDTH = 60;

    private InvoicePrinter() {}

    /**
     * Prints the invoice to console.
     */
    public static void printToConsole(Order order) {
        System.out.println(generateInvoiceString(order));
    }

    /**
     * Exports the invoice to a text file.
     * @param order the completed order
     * @param filePath the file path to write to
     */
    public static void exportToFile(Order order, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(generateInvoiceString(order));
            System.out.println("✓ Đã xuất hóa đơn ra file: " + filePath);
        } catch (IOException e) {
            System.out.println("✗ Lỗi khi xuất file: " + e.getMessage());
        }
    }

    /**
     * Generates the full invoice as a formatted string.
     */
    public static String generateInvoiceString(Order order) {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(WIDTH);
        String thinLine = "-".repeat(WIDTH);

        sb.append("\n").append(line).append("\n");
        sb.append(center("HÓA ĐƠN BÁN HÀNG")).append("\n");
        sb.append(center(STORE_NAME)).append("\n");
        sb.append(center(STORE_ADDRESS)).append("\n");
        sb.append(center(STORE_PHONE)).append("\n");
        sb.append(line).append("\n");

        sb.append(String.format("  Mã hóa đơn : %s%n", order.getId()));
        sb.append(String.format("  Ngày        : %s%n", order.getOrderDate().format(DATE_FMT)));
        sb.append(String.format("  Thu ngân    : %s%n", order.getEmployeeName()));
        if (order.getCustomerName() != null) {
            sb.append(String.format("  Khách hàng  : %s%n", order.getCustomerName()));
        }
        sb.append(thinLine).append("\n");

        // Header row
        sb.append(String.format("  %-25s %5s %12s %12s%n", "Sản phẩm", "SL", "Đơn giá", "T.Tiền"));
        sb.append(thinLine).append("\n");

        // Item rows
        for (OrderItem item : order.getItems()) {
            String name = item.getProductName();
            if (name.length() > 25) name = name.substring(0, 22) + "...";
            sb.append(String.format("  %-25s %5d %12s %12s%n",
                    name,
                    item.getQuantity(),
                    CurrencyFormatter.format(item.getUnitPrice()),
                    CurrencyFormatter.format(item.getSubTotal())));

            // Show discount if applicable
            if (item.getOriginalPrice() > item.getUnitPrice()) {
                sb.append(String.format("    (Gốc: %s, Giảm: %s)%n",
                        CurrencyFormatter.format(item.getOriginalPrice()),
                        CurrencyFormatter.format(item.getOriginalPrice() - item.getUnitPrice())));
            }
        }

        sb.append(thinLine).append("\n");
        sb.append(String.format("  %-40s %15s%n", "Tạm tính:",
                CurrencyFormatter.format(order.getSubTotal())));

        if (order.getCustomerDiscount() > 0) {
            sb.append(String.format("  %-40s %15s%n",
                    String.format("Giảm giá KH (%.0f%%):", order.getCustomerDiscountRate() * 100),
                    "-" + CurrencyFormatter.format(order.getCustomerDiscount())));
        }

        sb.append(line).append("\n");
        sb.append(String.format("  %-40s %15s%n", "TỔNG THANH TOÁN:",
                CurrencyFormatter.format(order.getFinalAmount())));
        sb.append(line).append("\n");

        // Payment info
        if (order.getPaymentMethod() != null) {
            sb.append(String.format("  Thanh toán  : %s%n", order.getPaymentMethod().getDisplayName()));
        }
        if (order.getPaymentDetails() != null) {
            sb.append(String.format("  Chi tiết    : %s%n", order.getPaymentDetails()));
        }

        // Reward points
        int points = order.calculateRewardPoints();
        if (points > 0 && order.getCustomerId() != null) {
            sb.append(String.format("  Điểm tích lũy: +%d điểm%n", points));
        }

        sb.append(thinLine).append("\n");
        sb.append(center("Cảm ơn quý khách đã mua hàng!")).append("\n");
        sb.append(center("Hẹn gặp lại!")).append("\n");
        sb.append(line).append("\n");

        return sb.toString();
    }

    /** Centers a string within the invoice width. */
    private static String center(String text) {
        int padding = (WIDTH - text.length()) / 2;
        if (padding <= 0) return text;
        return " ".repeat(padding) + text;
    }
}
