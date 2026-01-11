package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.product.domain.model.ProductStatus;

public record ProductResponse(
        Long id,
        Long sellerId,
        Long brandId,
        Long categoryId,
        String name,
        String description,
        ProductStatus status,
        int basePrice,
        String currency,
        int stockQuantity
) {
    public static ProductResponse from(ProductCommandUseCase.ProductResult result) {
        return new ProductResponse(
                result.id(),
                result.sellerId(),
                result.brandId(),
                result.categoryId(),
                result.name(),
                result.description(),
                result.status(),
                result.basePrice(),
                result.currency(),
                result.stockQuantity()
        );
    }
}
