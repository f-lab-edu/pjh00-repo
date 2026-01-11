package com.pjh.product.application.port.dto;

import java.util.List;

public record CatalogSearchEntry(
        Long productId,
        Long brandId,
        String productName,
        String brandName,
        String categoryPath,
        String thumbnailUrl,
        PriceSummary price,
        InventorySummary inventory,
        List<String> badges,
        long popularityScore
) {
}
