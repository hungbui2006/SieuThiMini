package com.supermarket.repository.impl;

import com.supermarket.model.User;
import com.supermarket.repository.UserRepository;

import java.util.*;

/**
 * In-memory implementation of UserRepository.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> storage = new LinkedHashMap<>();

    @Override
    public void save(User entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(User entity) {
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
    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }
}
