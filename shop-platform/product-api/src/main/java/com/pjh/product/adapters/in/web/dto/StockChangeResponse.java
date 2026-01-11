package com.pjh.product.adapters.in.web.dto;

import com.pjh.product.application.port.in.ProductCommandUseCase;

public record StockChangeResponse(Long productId, int oldStock, int newStock) {

    public static StockChangeResponse from(ProductCommandUseCase.StockChangeResult result) {
        return new StockChangeResponse(result.productId(), result.oldStock(), result.newStock());
    }
}
