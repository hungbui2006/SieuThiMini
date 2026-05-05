package com.supermarket.exception;

/**
 * Thrown when a requested entity cannot be found in the repository.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
