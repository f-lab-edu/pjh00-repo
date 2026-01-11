package com.pjh.product.application.port.dto;

import java.util.List;

public record CatalogProductView(
        Long productId,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryPath,
        String productName,
        String shortDescription,
        PriceSummary price,
        InventorySummary inventory,
        List<String> imageUrls,
        String thumbnailUrl,
        List<String> badges,
        List<String> highlights,
        long popularityScore,
        long updatedAtEpochMillis
) {
}
