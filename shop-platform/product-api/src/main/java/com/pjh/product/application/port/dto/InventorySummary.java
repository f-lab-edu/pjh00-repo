package com.pjh.product.application.port.dto;

public record InventorySummary(int stockQuantity, boolean inStock) {

    public static InventorySummary of(int stockQuantity) {
        return new InventorySummary(stockQuantity, stockQuantity > 0);
    }
}
