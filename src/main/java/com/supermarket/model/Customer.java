package com.supermarket.model;

import com.supermarket.model.enums.LoyaltyTier;

/**
 * Khách hàng thân thiết với chương trình tích điểm.
 */
public class Customer {
    private String id;
    private String name;
    private String phone;
    private String email;
    private LoyaltyTier tier;
    private int rewardPoints;

    public Customer() {
        this.tier = LoyaltyTier.THUONG;
        this.rewardPoints = 0;
    }

    public Customer(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.tier = LoyaltyTier.THUONG;
        this.rewardPoints = 0;
    }

    /** Tích điểm và tự động nâng hạng nếu đủ điều kiện. */
    public void addPoints(int points) {
        this.rewardPoints += points;
        updateTier();
    }

    /** Lấy tỷ lệ giảm giá theo hạng thành viên. */
    public double getDiscountRate() {
        return tier.getDiscountRate();
    }

    /** Tự động nâng hạng dựa trên điểm tích lũy. */
    private void updateTier() {
        this.tier = LoyaltyTier.fromPoints(this.rewardPoints);
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    public void setTier(LoyaltyTier tier) {
        this.tier = tier;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
        updateTier();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - SĐT: %s - Hạng: %s (%d điểm)",
                id, name, phone, tier.getDisplayName(), rewardPoints);
    }
}
