package com.supermarket.repository.impl;

import com.supermarket.model.Order;
import com.supermarket.repository.OrderRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of OrderRepository.
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> storage = new LinkedHashMap<>();

    @Override
    public void save(Order entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Order entity) {
        if (storage.containsKey(entity.getId())) {
            storage.put(entity.getId(), entity);
        }
    }

    @Override
    public boolean deleteById(String id) {
        return storage.remove(id) != null;
    }

    @Override
    public int count() {
        return storage.size();
    }

    @Override
    public List<Order> findByDate(LocalDate date) {
        return storage.values().stream()
                .filter(o -> o.getOrderDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByDateRange(LocalDate from, LocalDate to) {
        return storage.values().stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderDate().toLocalDate();
                    return !orderDate.isBefore(from) && !orderDate.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByEmployeeId(String employeeId) {
        return storage.values().stream()
                .filter(o -> employeeId.equals(o.getEmployeeId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return storage.values().stream()
                .filter(o -> customerId != null && customerId.equals(o.getCustomerId()))
                .collect(Collectors.toList());
    }
}
