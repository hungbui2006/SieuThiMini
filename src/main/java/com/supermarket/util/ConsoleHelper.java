package com.supermarket.util;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility class providing helper methods for console I/O with input validation.
 * All prompts are in Vietnamese.
 */
public final class ConsoleHelper {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String SEPARATOR = "═══════════════════════════════════════════════════════════════";
    private static final String THIN_SEP = "───────────────────────────────────────────────────────────────";

    private ConsoleHelper() {}

    /**
     * Reads an integer from the user with validation and retry.
     * @param prompt the prompt message in Vietnamese
     * @return a valid integer
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠ Vui lòng nhập số nguyên hợp lệ!");
            }
        }
    }

    /**
     * Reads an integer within a specified range.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("⚠ Vui lòng nhập số từ %d đến %d!%n", min, max);
        }
    }

    /**
     * Reads a double from the user with validation.
     */
    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Vui lòng nhập số hợp lệ!");
            }
        }
    }

    /**
     * Reads a positive double (greater than 0).
     */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);
            if (value > 0) return value;
            System.out.println("⚠ Giá trị phải lớn hơn 0!");
        }
    }

    /**
     * Reads a non-empty string from the user.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("⚠ Không được để trống!");
        }
    }

    /**
     * Reads a string, allows empty (returns default value if empty).
     */
    public static String readStringOptional(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Asks for a Yes/No confirmation.
     * @return true if the user answers 'y' or 'Y'
     */
    public static boolean confirm(String prompt) {
        System.out.print(prompt + " (c/k): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("c") || input.equals("co") || input.equals("có") || input.equals("y");
    }

    /**
     * Reads a Vietnamese phone number (must start with 0, 10-11 digits).
     */
    public static String readPhone(String prompt) {
        while (true) {
            String phone = readString(prompt);
            if (phone.matches("^0\\d{9,10}$")) {
                return phone;
            }
            System.out.println("⚠ Số điện thoại không hợp lệ! (Phải bắt đầu bằng 0, 10-11 số)");
        }
    }

    /**
     * Pauses execution and waits for user to press Enter.
     */
    public static void pressEnterToContinue() {
        System.out.print("\nNhấn Enter để tiếp tục...");
        scanner.nextLine();
    }

    /**
     * Prints a section header with decorative borders.
     */
    public static void printHeader(String title) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  " + title);
        System.out.println(SEPARATOR);
    }

    /**
     * Prints a thin separator line.
     */
    public static void printSeparator() {
        System.out.println(THIN_SEP);
    }

    /**
     * Clears the console screen (best effort).
     */
    public static void clearScreen() {
        System.out.print("\n".repeat(50));
    }

    /**
     * Prints a success message.
     */
    public static void printSuccess(String message) {
        System.out.println("✓ " + message);
    }

    /**
     * Prints a warning message.
     */
    public static void printWarning(String message) {
        System.out.println("⚠ " + message);
    }

    /**
     * Prints an error message.
     */
    public static void printError(String message) {
        System.out.println("✗ " + message);
    }
}
