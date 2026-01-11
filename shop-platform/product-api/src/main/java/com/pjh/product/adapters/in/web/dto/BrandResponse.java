package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.BrandCommandUseCase;

public record BrandResponse(Long id, String name) {

    public static BrandResponse from(BrandCommandUseCase.BrandResult result) {
        return new BrandResponse(result.id(), result.name());
    }
}
