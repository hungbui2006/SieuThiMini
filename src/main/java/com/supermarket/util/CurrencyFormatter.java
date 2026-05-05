package com.supermarket.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for formatting currency values in Vietnamese Dong (VND).
 * Formats numbers with dot separators and "đ" suffix (e.g., 1.250.000đ).
 */
public final class CurrencyFormatter {

    private static final DecimalFormat VND_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        VND_FORMAT = new DecimalFormat("#,###", symbols);
    }

    private CurrencyFormatter() {
        // Prevent instantiation of utility class
    }

    /**
     * Formats a numeric amount to Vietnamese currency string.
     * @param amount the amount in VND
     * @return formatted string, e.g. "1.250.000đ"
     */
    public static String format(double amount) {
        return VND_FORMAT.format(amount) + "đ";
    }

    /**
     * Formats a numeric amount with explicit "VND" label.
     * @param amount the amount in VND
     * @return formatted string, e.g. "1.250.000 VND"
     */
    public static String formatWithLabel(double amount) {
        return VND_FORMAT.format(amount) + " VND";
    }
}
