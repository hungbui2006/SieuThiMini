package com.supermarket.strategy;

import com.supermarket.util.CurrencyFormatter;

/**
 * Thanh toán bằng thẻ ngân hàng.
 */
public class CardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardHolderName;

    public CardPayment() {}

    public CardPayment(String cardNumber, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public boolean processPayment(double amount) {
        // Kiểm tra số thẻ hợp lệ
        if (cardNumber == null || cardNumber.length() < 10) {
            System.out.println("⚠ Số thẻ không hợp lệ!");
            return false;
        }
        System.out.println("✓ Thanh toán thẻ ngân hàng thành công!");
        System.out.println("  Số thẻ : ****" + cardNumber.substring(cardNumber.length() - 4));
        System.out.println("  Số tiền: " + CurrencyFormatter.format(amount));
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Thẻ ngân hàng";
    }

    @Override
    public String getPaymentDetails() {
        String maskedCard = (cardNumber != null && cardNumber.length() >= 4)
                ? "****" + cardNumber.substring(cardNumber.length() - 4) : "N/A";
        return "Thẻ ngân hàng - Số thẻ: " + maskedCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
}
