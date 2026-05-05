package com.supermarket.repository.impl;

import com.supermarket.model.Customer;
import com.supermarket.repository.CustomerRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of CustomerRepository.
 */
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<String, Customer> storage = new LinkedHashMap<>();

    @Override
    public void save(Customer entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Customer entity) {
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
    public Optional<Customer> findByPhone(String phone) {
        return storage.values().stream()
                .filter(c -> phone.equals(c.getPhone()))
                .findFirst();
    }

    @Override
    public List<Customer> findByName(String keyword) {
        String lower = keyword.toLowerCase();
        return storage.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
