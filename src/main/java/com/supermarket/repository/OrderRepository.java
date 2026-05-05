package com.supermarket.repository;

import com.supermarket.model.Order;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface specific to Order entities.
 */
public interface OrderRepository extends GenericRepository<Order> {

    /**
     * Finds all orders placed on a specific date.
     */
    List<Order> findByDate(LocalDate date);

    /**
     * Finds all orders within a date range.
     */
    List<Order> findByDateRange(LocalDate from, LocalDate to);

    /**
     * Finds all orders processed by a specific employee.
     */
    List<Order> findByEmployeeId(String employeeId);

    /**
     * Finds all orders for a specific customer.
     */
    List<Order> findByCustomerId(String customerId);
}
