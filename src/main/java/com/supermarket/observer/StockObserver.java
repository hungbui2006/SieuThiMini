package com.supermarket.observer;

import com.supermarket.model.Product;

/**
 * Interface Observer — nhận thông báo khi tồn kho thay đổi.
 */
public interface StockObserver {

    /** Được gọi khi số lượng tồn kho của sản phẩm thay đổi. */
    void onStockChanged(Product product);
}
