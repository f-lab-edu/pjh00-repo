package com.pjh.product.application.port.dto;

import java.util.Collections;
import java.util.Set;

public record CatalogSearchQuery(
        String keyword,
        Long categoryId,
        Integer minPrice,
        Integer maxPrice,
        Set<Long> brandIds,
        SortOption sortOption,
        int page,
        int size
) {

    public CatalogSearchQuery {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater or equal to zero");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
        if (brandIds == null) {
            brandIds = Collections.emptySet();
        }
    }

    public enum SortOption {
        RELEVANCE,
        PRICE_ASC,
        PRICE_DESC,
        POPULARITY,
        NEWEST
    }
}
