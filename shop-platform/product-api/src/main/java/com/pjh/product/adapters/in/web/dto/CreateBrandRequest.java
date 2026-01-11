package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.BrandCommandUseCase;
import jakarta.validation.constraints.NotBlank;

public record CreateBrandRequest(Long id, @NotBlank String name) {

    public BrandCommandUseCase.CreateBrandCommand toCommand() {
        return new BrandCommandUseCase.CreateBrandCommand(id, name);
    }
}
