package com.supermarket.strategy;

/**
 * Interface xử lý thanh toán — mỗi phương thức thanh toán cài đặt riêng.
 */
public interface PaymentStrategy {

    /** Xử lý thanh toán với số tiền cho trước. */
    boolean processPayment(double amount);

    /** Tên phương thức thanh toán. */
    String getPaymentMethodName();

    /** Chi tiết thanh toán (in trên hóa đơn). */
    String getPaymentDetails();
}
