package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.AuthenticationException;
import com.supermarket.model.User;
import com.supermarket.repository.UserRepository;

/**
 * Service responsible for user authentication and session management.
 */
public class AuthService {

    private final UserRepository userRepo;
    private User currentUser;

    public AuthService() {
        this.userRepo = DataStore.getInstance().getUserRepository();
    }

    /**
     * Authenticates a user with username and password.
     * @param username the login username
     * @param password the login password
     * @return the authenticated User object
     * @throws AuthenticationException if authentication fails
     */
    public User login(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException(
                        "Tên đăng nhập không tồn tại: " + username));

        if (!user.isActive()) {
            throw new AuthenticationException("Tài khoản đã bị vô hiệu hóa!");
        }

        if (!user.authenticate(password)) {
            throw new AuthenticationException("Mật khẩu không đúng!");
        }

        this.currentUser = user;
        return user;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     * @return current user, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if someone is currently logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
