package com.supermarket.model.enums;

/**
 * Enum representing customer loyalty tiers with associated discount rates.
 * Hạng thành viên khách hàng thân thiết.
 */
public enum LoyaltyTier {
    THUONG("Thường", 0.0, 0),          // Normal tier - no discount
    SILVER("Silver", 0.03, 500),        // 3% discount, requires 500 points
    GOLD("Gold", 0.05, 1500),           // 5% discount, requires 1500 points
    PLATINUM("Platinum", 0.10, 5000);   // 10% discount, requires 5000 points

    private final String displayName;
    private final double discountRate;
    private final int requiredPoints;

    LoyaltyTier(String displayName, double discountRate, int requiredPoints) {
        this.displayName = displayName;
        this.discountRate = discountRate;
        this.requiredPoints = requiredPoints;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    /**
     * Determines the appropriate tier based on accumulated reward points.
     * @param points current reward points of the customer
     * @return the highest tier the customer qualifies for
     */
    public static LoyaltyTier fromPoints(int points) {
        if (points >= PLATINUM.requiredPoints) return PLATINUM;
        if (points >= GOLD.requiredPoints) return GOLD;
        if (points >= SILVER.requiredPoints) return SILVER;
        return THUONG;
    }
}
