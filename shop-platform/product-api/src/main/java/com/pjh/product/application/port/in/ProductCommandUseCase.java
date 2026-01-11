package com.pjh.product.application.port.in;

import com.pjh.product.domain.model.ProductStatus;

public interface ProductCommandUseCase {

    ProductResult createProduct(CreateProductCommand command);

    ProductResult updateProduct(Long productId, UpdateProductCommand command);

    PriceChangeResult changePrice(Long productId, ChangePriceCommand command);

    StockChangeResult changeStock(Long productId, ChangeStockCommand command);

    void deleteProduct(Long productId);

    record CreateProductCommand(
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
    }

    record UpdateProductCommand(
            Long brandId,
            Long categoryId,
            String name,
            String description,
            ProductStatus status
    ) {
    }

    record ChangePriceCommand(int newPrice, String reason) {
    }

    record ChangeStockCommand(int newStock, String reason) {
    }

    record ProductResult(
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
    }

    record PriceChangeResult(Long productId, int oldPrice, int newPrice) {
    }

    record StockChangeResult(Long productId, int oldStock, int newStock) {
    }
}
