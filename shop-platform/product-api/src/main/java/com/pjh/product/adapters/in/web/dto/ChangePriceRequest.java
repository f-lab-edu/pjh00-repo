package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePriceRequest(
        @NotNull @Min(0) Integer newPrice,
        @Size(max = 200) String reason
) {
    public ProductCommandUseCase.ChangePriceCommand toCommand() {
        return new ProductCommandUseCase.ChangePriceCommand(newPrice, reason);
    }
}
