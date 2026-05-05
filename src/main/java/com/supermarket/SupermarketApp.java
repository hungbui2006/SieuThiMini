package com.supermarket;

import com.supermarket.view.console.ConsoleApp;

/**
 * Application entry point.
 * Ứng dụng Quản lý Siêu thị Bán hàng.
 *
 * @author OOP Course Project
 * @version 1.0
 */
public class SupermarketApp {

    public static void main(String[] args) {
        // Set console encoding for Vietnamese characters
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (Exception ignored) {}

        ConsoleApp app = new ConsoleApp();
        app.run();
    }
}
