package com.pjh.product.application.port.dto;

import java.util.Collections;
import java.util.List;

public record CatalogSearchResponse(List<CatalogSearchEntry> items, long totalCount, int page, int size) {

    public CatalogSearchResponse {
        if (items == null) {
            items = Collections.emptyList();
        }
    }

    public long totalPages() {
        if (size <= 0) {
            return 0L;
        }
        return (long) Math.ceil((double) totalCount / size);
    }
}
