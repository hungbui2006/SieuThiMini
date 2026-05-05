package com.supermarket.observer;

/**
 * Interface Subject — đối tượng được theo dõi tồn kho.
 */
public interface StockSubject {

    /** Đăng ký observer. */
    void addObserver(StockObserver observer);

    /** Hủy đăng ký observer. */
    void removeObserver(StockObserver observer);

    /** Thông báo cho tất cả observer khi tồn kho thay đổi. */
    void notifyObservers();
}
