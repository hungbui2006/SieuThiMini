package com.supermarket.strategy;

import com.supermarket.util.CurrencyFormatter;

/**
 * Thanh toán bằng ví điện tử (MoMo, ZaloPay, VietQR).
 */
public class EWalletPayment implements PaymentStrategy {
    private String walletType; // "MoMo", "ZaloPay", "VietQR"
    private String walletPhone;
    private String transactionId;

    public EWalletPayment() {}

    public EWalletPayment(String walletType, String walletPhone) {
        this.walletType = walletType;
        this.walletPhone = walletPhone;
    }

    @Override
    public boolean processPayment(double amount) {
        if (walletPhone == null || walletPhone.length() < 10) {
            System.out.println("⚠ Số điện thoại ví " + walletType + " không hợp lệ!");
            return false;
        }
        // Tạo mã giao dịch
        this.transactionId = walletType.toUpperCase() + System.currentTimeMillis();
        System.out.println("✓ Thanh toán qua " + walletType + " thành công!");
        System.out.println("  SĐT ví     : " + walletPhone);
        System.out.println("  Số tiền    : " + CurrencyFormatter.format(amount));
        System.out.println("  Mã giao dịch: " + transactionId);
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Ví " + walletType;
    }

    @Override
    public String getPaymentDetails() {
        return String.format("Ví %s - SĐT: %s - Mã GD: %s",
                walletType, walletPhone, transactionId != null ? transactionId : "N/A");
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getWalletPhone() {
        return walletPhone;
    }

    public void setWalletPhone(String walletPhone) {
        this.walletPhone = walletPhone;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
