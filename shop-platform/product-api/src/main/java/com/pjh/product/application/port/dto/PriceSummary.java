package com.pjh.product.application.port.dto;

public record PriceSummary(int basePrice, String currency, Integer discountedPrice) {

    public PriceSummary {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
    }

    public boolean hasDiscount() {
        return discountedPrice != null && discountedPrice > 0 && discountedPrice < basePrice;
    }
}
