package com.supermarket.view.console;

import com.supermarket.model.*;
import com.supermarket.model.enums.PaymentMethod;
import com.supermarket.service.*;
import com.supermarket.strategy.*;
import com.supermarket.util.*;

import java.util.List;

/**
 * Point of Sale console view — handles the complete checkout flow.
 * Tạo hóa đơn bán hàng.
 */
public class POSView {

    private final OrderService orderService;
    private final ProductService productService;
    private final CustomerService customerService;

    public POSView(OrderService orderService, ProductService productService,
                   CustomerService customerService) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
    }

    /**
     * Starts a new POS session for the given employee.
     */
    public void show(Employee cashier) {
        ConsoleHelper.printHeader("TẠO HÓA ĐƠN BÁN HÀNG");
        System.out.println("  Thu ngân: " + cashier.getFullName());
        ConsoleHelper.printSeparator();

        Order order = orderService.createNewOrder(cashier);
        System.out.println("  Mã hóa đơn: " + order.getId());

        // POS loop
        while (true) {
            System.out.println("\n  --- GIỎ HÀNG ---");
            displayCart(order);
            System.out.println("\n  1. Thêm sản phẩm");
            System.out.println("  2. Sửa số lượng");
            System.out.println("  3. Xóa sản phẩm khỏi giỏ");
            System.out.println("  4. Tìm kiếm sản phẩm");
            System.out.println("  5. Gắn khách hàng thân thiết");
            System.out.println("  6. Thanh toán");
            System.out.println("  0. Hủy hóa đơn");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn: ", 0, 6);
            switch (choice) {
                case 1 -> addProductToCart(order);
                case 2 -> updateQuantity(order);
                case 3 -> removeFromCart(order);
                case 4 -> searchProduct();
                case 5 -> attachCustomer(order);
                case 6 -> {
                    if (order.getItems().isEmpty()) {
                        ConsoleHelper.printWarning("Giỏ hàng trống!");
                    } else {
                        checkout(order, cashier);
                        return;
                    }
                }
                case 0 -> {
                    if (ConsoleHelper.confirm("  Xác nhận hủy hóa đơn?")) {
                        System.out.println("  Đã hủy hóa đơn.");
                        return;
                    }
                }
            }
        }
    }

    private void addProductToCart(Order order) {
        String productId = ConsoleHelper.readString("  Nhập mã SP (hoặc tên để tìm): ");

        // Try as ID first
        try {
            Product product = productService.getProduct(productId);
            System.out.printf("  Sản phẩm: %s - %s (Tồn: %d %s)%n",
                    product.getName(),
                    CurrencyFormatter.format(product.calculateFinalPrice()),
                    product.getStockQuantity(), product.getUnit());
            int qty = ConsoleHelper.readInt("  Số lượng: ", 1, product.getStockQuantity());
            orderService.addToCart(order, product.getId(), qty);
            ConsoleHelper.printSuccess("Đã thêm vào giỏ hàng!");
        } catch (Exception e) {
            // Try as search keyword
            List<Product> results = productService.searchByName(productId);
            if (results.isEmpty()) {
                ConsoleHelper.printError("Không tìm thấy sản phẩm: " + productId);
                return;
            }
            System.out.println("  Kết quả tìm kiếm:");
            for (int i = 0; i < results.size(); i++) {
                Product p = results.get(i);
                System.out.printf("    %d. [%s] %s - %s (Tồn: %d)%n",
                        i + 1, p.getId(), p.getName(),
                        CurrencyFormatter.format(p.calculateFinalPrice()),
                        p.getStockQuantity());
            }
            int idx = ConsoleHelper.readInt("  Chọn SP (0 để hủy): ", 0, results.size());
            if (idx > 0) {
                Product selected = results.get(idx - 1);
                int qty = ConsoleHelper.readInt("  Số lượng: ", 1, selected.getStockQuantity());
                try {
                    orderService.addToCart(order, selected.getId(), qty);
                    ConsoleHelper.printSuccess("Đã thêm vào giỏ hàng!");
                } catch (Exception ex) {
                    ConsoleHelper.printError(ex.getMessage());
                }
            }
        }
    }

    private void updateQuantity(Order order) {
        if (order.getItems().isEmpty()) {
            ConsoleHelper.printWarning("Giỏ hàng trống!");
            return;
        }
        String productId = ConsoleHelper.readString("  Nhập mã SP cần sửa: ");
        int newQty = ConsoleHelper.readInt("  Số lượng mới (0 để xóa): ", 0, 9999);
        if (order.updateItemQuantity(productId, newQty)) {
            ConsoleHelper.printSuccess("Đã cập nhật số lượng.");
        } else {
            ConsoleHelper.printError("Không tìm thấy SP " + productId + " trong giỏ.");
        }
    }

    private void removeFromCart(Order order) {
        String productId = ConsoleHelper.readString("  Nhập mã SP cần xóa: ");
        if (order.removeItem(productId)) {
            ConsoleHelper.printSuccess("Đã xóa khỏi giỏ hàng.");
        } else {
            ConsoleHelper.printError("Không tìm thấy SP " + productId + " trong giỏ.");
        }
    }

    private void searchProduct() {
        String keyword = ConsoleHelper.readString("  Nhập từ khóa: ");
        List<Product> results = productService.searchByName(keyword);
        if (results.isEmpty()) {
            System.out.println("  Không tìm thấy.");
        } else {
            for (Product p : results) {
                System.out.printf("    [%s] %s - %s (Tồn: %d %s)%n",
                        p.getId(), p.getName(),
                        CurrencyFormatter.format(p.calculateFinalPrice()),
                        p.getStockQuantity(), p.getUnit());
            }
        }
    }

    private void attachCustomer(Order order) {
        System.out.println("  Tìm KH theo:");
        System.out.println("  1. Mã KH");
        System.out.println("  2. SĐT");
        int choice = ConsoleHelper.readInt("  Chọn: ", 1, 2);

        Customer customer = null;
        if (choice == 1) {
            String id = ConsoleHelper.readString("  Nhập mã KH: ");
            try { customer = customerService.getCustomer(id); } catch (Exception ignored) {}
        } else {
            String phone = ConsoleHelper.readString("  Nhập SĐT: ");
            customer = customerService.findByPhone(phone);
        }

        if (customer == null) {
            ConsoleHelper.printWarning("Không tìm thấy khách hàng.");
            if (ConsoleHelper.confirm("  Tạo khách hàng mới?")) {
                String id = IdGenerator.nextId(IdGenerator.CUSTOMER_PREFIX);
                String name = ConsoleHelper.readString("  Họ tên: ");
                String phone = ConsoleHelper.readPhone("  SĐT: ");
                String email = ConsoleHelper.readStringOptional("  Email: ", "");
                customer = new Customer(id, name, phone, email);
                customerService.addCustomer(customer);
                ConsoleHelper.printSuccess("Đã tạo KH mới: " + customer);
            } else {
                return;
            }
        }

        orderService.attachCustomer(order, customer.getId());
        ConsoleHelper.printSuccess("Đã gắn KH: " + customer.getName() +
                " (Hạng: " + customer.getTier().getDisplayName() +
                ", Giảm: " + String.format("%.0f%%", customer.getDiscountRate() * 100) + ")");
    }

    private void checkout(Order order, Employee cashier) {
        ConsoleHelper.printHeader("THANH TOÁN");
        displayCart(order);
        System.out.println();
        System.out.println("  Tổng thanh toán: " + CurrencyFormatter.format(order.getFinalAmount()));
        ConsoleHelper.printSeparator();

        // Choose payment method
        System.out.println("  Chọn phương thức thanh toán:");
        System.out.println("  1. Tiền mặt");
        System.out.println("  2. Thẻ ngân hàng");
        System.out.println("  3. Ví MoMo");
        System.out.println("  4. ZaloPay");
        System.out.println("  5. VietQR");
        int pmChoice = ConsoleHelper.readInt("  Chọn: ", 1, 5);

        PaymentStrategy strategy;
        PaymentMethod method;
        switch (pmChoice) {
            case 1 -> {
                method = PaymentMethod.CASH;
                double cash = ConsoleHelper.readPositiveDouble("  Tiền khách đưa: ");
                strategy = new CashPayment(cash);
            }
            case 2 -> {
                method = PaymentMethod.CARD;
                String cardNum = ConsoleHelper.readString("  Số thẻ: ");
                String holder = ConsoleHelper.readString("  Tên chủ thẻ: ");
                strategy = new CardPayment(cardNum, holder);
            }
            case 3 -> {
                method = PaymentMethod.MOMO;
                String phone = ConsoleHelper.readString("  SĐT ví MoMo: ");
                strategy = new EWalletPayment("MoMo", phone);
            }
            case 4 -> {
                method = PaymentMethod.ZALOPAY;
                String phone = ConsoleHelper.readString("  SĐT ZaloPay: ");
                strategy = new EWalletPayment("ZaloPay", phone);
            }
            default -> {
                method = PaymentMethod.VIETQR;
                String phone = ConsoleHelper.readString("  SĐT VietQR: ");
                strategy = new EWalletPayment("VietQR", phone);
            }
        }

        // Process payment using Strategy pattern
        System.out.println();
        boolean success = strategy.processPayment(order.getFinalAmount());
        if (!success) {
            ConsoleHelper.printError("Thanh toán thất bại!");
            return;
        }

        // Complete the order
        orderService.completeOrder(order, method, strategy.getPaymentDetails());

        // Record sales for employee commission
        cashier.addSaleAmount(order.getFinalAmount());

        // Print invoice
        InvoicePrinter.printToConsole(order);

        // Offer to export invoice to file
        if (ConsoleHelper.confirm("  Xuất hóa đơn ra file?")) {
            String fileName = "hoadon_" + order.getId() + ".txt";
            InvoicePrinter.exportToFile(order, fileName);
        }

        ConsoleHelper.pressEnterToContinue();
    }

    private void displayCart(Order order) {
        if (order.getItems().isEmpty()) {
            System.out.println("  (Giỏ hàng trống)");
            return;
        }
        System.out.printf("  %-6s %-25s %5s %12s %12s%n", "Mã SP", "Tên", "SL", "Đơn giá", "T.Tiền");
        ConsoleHelper.printSeparator();
        for (OrderItem item : order.getItems()) {
            String name = item.getProductName();
            if (name.length() > 25) name = name.substring(0, 22) + "...";
            System.out.printf("  %-6s %-25s %5d %12s %12s%n",
                    item.getProductId(), name, item.getQuantity(),
                    CurrencyFormatter.format(item.getUnitPrice()),
                    CurrencyFormatter.format(item.getSubTotal()));
        }
        ConsoleHelper.printSeparator();
        System.out.printf("  %-40s %15s%n", "Tạm tính:", CurrencyFormatter.format(order.getSubTotal()));
        if (order.getCustomerDiscount() > 0) {
            System.out.printf("  %-40s %15s%n",
                    "Giảm giá KH (" + String.format("%.0f%%", order.getCustomerDiscountRate() * 100) + "):",
                    "-" + CurrencyFormatter.format(order.getCustomerDiscount()));
        }
        System.out.printf("  %-40s %15s%n", "TỔNG:", CurrencyFormatter.format(order.getFinalAmount()));
    }
}
