package com.supermarket.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs with entity-specific prefixes.
 * Example: SP001 for products, KH001 for customers, HD001 for orders.
 */
public final class IdGenerator {

    private static final Map<String, AtomicInteger> counters = new HashMap<>();

    // Standard prefixes for each entity type
    public static final String PRODUCT_PREFIX = "SP";
    public static final String CATEGORY_PREFIX = "DM";
    public static final String SUPPLIER_PREFIX = "NCC";
    public static final String EMPLOYEE_PREFIX = "NV";
    public static final String CUSTOMER_PREFIX = "KH";
    public static final String ORDER_PREFIX = "HD";
    public static final String IMPORT_PREFIX = "NH";
    public static final String USER_PREFIX = "TK";

    private IdGenerator() {
        // Prevent instantiation
    }

    /**
     * Generates the next unique ID for the given prefix.
     * @param prefix the entity prefix (e.g., "SP" for sản phẩm)
     * @return a formatted ID like "SP001", "SP002", etc.
     */
    public static synchronized String nextId(String prefix) {
        counters.putIfAbsent(prefix, new AtomicInteger(0));
        int nextVal = counters.get(prefix).incrementAndGet();
        return String.format("%s%03d", prefix, nextVal);
    }

    /**
     * Resets the counter for a given prefix. Useful when loading from persistence
     * to avoid duplicate IDs.
     * @param prefix the entity prefix
     * @param lastValue the last known ID value (numeric part)
     */
    public static synchronized void resetCounter(String prefix, int lastValue) {
        counters.put(prefix, new AtomicInteger(lastValue));
    }

    /**
     * Extracts the numeric part from an ID string.
     * @param id full ID string (e.g., "SP015")
     * @param prefix the prefix to strip
     * @return numeric value (e.g., 15)
     */
    public static int extractNumber(String id, String prefix) {
        try {
            return Integer.parseInt(id.substring(prefix.length()));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
