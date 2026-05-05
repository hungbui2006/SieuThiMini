package com.supermarket.model;

import com.supermarket.model.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Hóa đơn bán hàng — chứa danh sách sản phẩm, thông tin khách, thanh toán.
 */
public class Order {
    private String id;
    private LocalDateTime orderDate;
    private String employeeId;       // Nhân viên thu ngân
    private String employeeName;
    private String customerId;       // Có thể null nếu là khách vãng lai
    private String customerName;
    private List<OrderItem> items;
    private double subTotal;         // Tổng tiền chưa giảm
    private double customerDiscount; // Giảm giá thành viên
    private double totalDiscount;    // Tổng giảm giá
    private double finalAmount;      // Số tiền khách phải trả
    private PaymentMethod paymentMethod;
    private String paymentDetails;
    private boolean completed;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.completed = false;
    }

    public Order(String id, String employeeId, String employeeName) {
        this();
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    /** Thêm sản phẩm vào đơn hàng. Nếu SP đã có thì cộng thêm số lượng. */
    public void addItem(OrderItem item) {
        // Kiểm tra SP đã có trong giỏ chưa
        for (OrderItem existing : items) {
            if (existing.getProductId().equals(item.getProductId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                recalculate();
                return;
            }
        }
        items.add(item);
        recalculate();
    }

    /** Xóa sản phẩm khỏi đơn. */
    public boolean removeItem(String productId) {
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (removed) recalculate();
        return removed;
    }

    /** Cập nhật số lượng SP trong giỏ. */
    public boolean updateItemQuantity(String productId, int newQuantity) {
        for (OrderItem item : items) {
            if (item.getProductId().equals(productId)) {
                if (newQuantity <= 0) {
                    return removeItem(productId);
                }
                item.setQuantity(newQuantity);
                recalculate();
                return true;
            }
        }
        return false;
    }

    /** Tính lại tổng tiền, giảm giá, thành tiền. */
    public void recalculate() {
        this.subTotal = 0;
        this.totalDiscount = 0;
        for (OrderItem item : items) {
            this.subTotal += item.getSubTotal();
            this.totalDiscount += item.getDiscountAmount();
        }
        // Áp dụng giảm giá thành viên
        this.customerDiscount = this.subTotal * getCustomerDiscountRate();
        this.totalDiscount += this.customerDiscount;
        this.finalAmount = this.subTotal - this.customerDiscount;
    }

    /** Tỷ lệ giảm giá của khách (0 nếu không có thẻ). */
    private double customerDiscountRate = 0;

    public void setCustomerDiscountRate(double rate) {
        this.customerDiscountRate = rate;
        recalculate();
    }

    public double getCustomerDiscountRate() {
        return customerDiscountRate;
    }

    /** Tính điểm tích lũy: mỗi 10.000đ = 1 điểm. */
    public int calculateRewardPoints() {
        return (int) (finalAmount / 10000);
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getCustomerDiscount() {
        return customerDiscount;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
