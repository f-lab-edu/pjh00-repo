package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeStockRequest(
        @NotNull @Min(0) Integer newStock,
        @Size(max = 200) String reason
) {
    public ProductCommandUseCase.ChangeStockCommand toCommand() {
        return new ProductCommandUseCase.ChangeStockCommand(newStock, reason);
    }
}
