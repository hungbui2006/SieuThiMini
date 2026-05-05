package com.supermarket.repository.impl;

import com.supermarket.model.Supplier;
import com.supermarket.repository.SupplierRepository;

import java.util.*;

/**
 * In-memory implementation of SupplierRepository.
 */
public class InMemorySupplierRepository implements SupplierRepository {

    private final Map<String, Supplier> storage = new LinkedHashMap<>();

    @Override
    public void save(Supplier entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Supplier> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Supplier> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Supplier entity) {
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
}
