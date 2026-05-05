package com.supermarket.repository;

import com.supermarket.model.User;

import java.util.Optional;

/**
 * Repository interface specific to User entities.
 */
public interface UserRepository extends GenericRepository<User> {

    /**
     * Finds a user by their username.
     */
    Optional<User> findByUsername(String username);
}
