package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;

public record PriceChangeResponse(Long productId, int oldPrice, int newPrice) {

    public static PriceChangeResponse from(ProductCommandUseCase.PriceChangeResult result) {
        return new PriceChangeResponse(result.productId(), result.oldPrice(), result.newPrice());
    }
}
