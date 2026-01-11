package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.product.domain.model.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        Long id,
        @NotNull Long sellerId,
        @NotNull Long brandId,
        @NotNull Long categoryId,
        @NotBlank String name,
        String description,
        @NotNull ProductStatus status,
        @Min(0) int basePrice,
        @NotBlank String currency,
        @Min(0) int stockQuantity
) {
    public ProductCommandUseCase.CreateProductCommand toCommand() {
        return new ProductCommandUseCase.CreateProductCommand(
                id,
                sellerId,
                brandId,
                categoryId,
                name,
                description,
                status,
                basePrice,
                currency,
                stockQuantity
        );
    }
}
