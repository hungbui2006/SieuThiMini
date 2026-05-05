package com.supermarket.repository;

import com.supermarket.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface specific to Customer entities.
 */
public interface CustomerRepository extends GenericRepository<Customer> {

    /**
     * Finds a customer by phone number.
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Finds customers by name (partial match).
     */
    List<Customer> findByName(String keyword);
}
