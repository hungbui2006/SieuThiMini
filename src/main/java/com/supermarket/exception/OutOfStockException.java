package com.supermarket.exception;

/**
 * Thrown when a sale cannot be completed due to insufficient stock.
 */
public class OutOfStockException extends RuntimeException {
    private final String productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    public OutOfStockException(String productId, int requestedQuantity, int availableQuantity) {
        super(String.format("Sản phẩm %s không đủ tồn kho! Yêu cầu: %d, Tồn kho: %d",
                productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
