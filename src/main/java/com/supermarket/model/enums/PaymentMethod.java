package com.supermarket.model.enums;

/**
 * Enum representing available payment methods.
 * Phương thức thanh toán hỗ trợ tại siêu thị.
 */
public enum PaymentMethod {
    CASH("Tiền mặt"),
    CARD("Thẻ ngân hàng"),
    MOMO("Ví MoMo"),
    ZALOPAY("ZaloPay"),
    VIETQR("VietQR");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
