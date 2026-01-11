package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.product.domain.model.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductRequest(
        @NotNull Long brandId,
        @NotNull Long categoryId,
        @NotBlank String name,
        String description,
        @NotNull ProductStatus status
) {
    public ProductCommandUseCase.UpdateProductCommand toCommand() {
        return new ProductCommandUseCase.UpdateProductCommand(brandId, categoryId, name, description, status);
    }
}
