package com.supermarket.exception;

/**
 * Thrown when authentication fails (wrong username/password or inactive account).
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
