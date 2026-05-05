package com.supermarket.repository.impl;

import com.supermarket.model.GoodsImport;
import com.supermarket.repository.GoodsImportRepository;

import java.util.*;

/**
 * In-memory implementation of GoodsImportRepository.
 */
public class InMemoryGoodsImportRepository implements GoodsImportRepository {

    private final Map<String, GoodsImport> storage = new LinkedHashMap<>();

    @Override
    public void save(GoodsImport entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<GoodsImport> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<GoodsImport> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(GoodsImport entity) {
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
