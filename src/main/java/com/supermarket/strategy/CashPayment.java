package com.supermarket.strategy;

import com.supermarket.util.CurrencyFormatter;

/**
 * Thanh toán bằng tiền mặt.
 */
public class CashPayment implements PaymentStrategy {
    private double cashTendered;
    private double changeAmount;

    public CashPayment() {}

    public CashPayment(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    @Override
    public boolean processPayment(double amount) {
        if (cashTendered < amount) {
            System.out.println("⚠ Số tiền khách đưa không đủ!");
            return false;
        }
        this.changeAmount = cashTendered - amount;
        System.out.println("✓ Thanh toán tiền mặt thành công!");
        System.out.println("  Tiền khách đưa : " + CurrencyFormatter.format(cashTendered));
        System.out.println("  Tiền thừa      : " + CurrencyFormatter.format(changeAmount));
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Tiền mặt";
    }

    @Override
    public String getPaymentDetails() {
        return String.format("Tiền mặt - Khách đưa: %s, Thừa: %s",
                CurrencyFormatter.format(cashTendered),
                CurrencyFormatter.format(changeAmount));
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    public double getChangeAmount() {
        return changeAmount;
    }
}
