package com.supermarket.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface Repository chung — các thao tác CRUD cơ bản.
 * @param <T> kiểu entity
 */
public interface GenericRepository<T> {

    /** Lưu entity mới. */
    void save(T entity);

    /** Tìm entity theo mã. */
    Optional<T> findById(String id);

    /** Lấy tất cả. */
    List<T> findAll();

    /** Cập nhật entity. */
    void update(T entity);

    /** Xóa entity theo mã. */
    boolean deleteById(String id);

    /** Đếm tổng số entity. */
    int count();
}
