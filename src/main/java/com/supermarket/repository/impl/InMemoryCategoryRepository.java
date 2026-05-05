package com.supermarket.repository.impl;

import com.supermarket.model.Category;
import com.supermarket.repository.CategoryRepository;

import java.util.*;

/**
 * In-memory implementation of CategoryRepository.
 */
public class InMemoryCategoryRepository implements CategoryRepository {

    private final Map<String, Category> storage = new LinkedHashMap<>();

    @Override
    public void save(Category entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Category entity) {
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
