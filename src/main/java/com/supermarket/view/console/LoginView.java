package com.supermarket.view.console;

import com.supermarket.exception.AuthenticationException;
import com.supermarket.model.User;
import com.supermarket.model.enums.UserRole;
import com.supermarket.service.AuthService;
import com.supermarket.util.ConsoleHelper;

/**
 * Login view — handles user authentication via console.
 */
public class LoginView {

    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Displays the login form and authenticates the user.
     * @return the authenticated User, or null if the user chose to exit
     */
    public User showLogin() {
        ConsoleHelper.printHeader("ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ SIÊU THỊ");
        System.out.println("  Tài khoản mặc định: admin / admin123");
        System.out.println("  Nhân viên: nv01 / nv123");
        ConsoleHelper.printSeparator();

        while (true) {
            String username = ConsoleHelper.readString("  Tên đăng nhập (hoặc 'exit' để thoát): ");
            if (username.equalsIgnoreCase("exit")) {
                return null;
            }

            String password = ConsoleHelper.readString("  Mật khẩu: ");

            try {
                User user = authService.login(username, password);
                ConsoleHelper.printSuccess("Đăng nhập thành công! Xin chào, " + user.getFullName());
                System.out.println("  Vai trò: " + user.getRole().getDisplayName());
                ConsoleHelper.pressEnterToContinue();
                return user;
            } catch (AuthenticationException e) {
                ConsoleHelper.printError(e.getMessage());
                System.out.println();
            }
        }
    }
}
