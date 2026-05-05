package com.supermarket.exception;

/**
 * Thrown when an entity with a duplicate ID or unique field is detected.
 */
public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
