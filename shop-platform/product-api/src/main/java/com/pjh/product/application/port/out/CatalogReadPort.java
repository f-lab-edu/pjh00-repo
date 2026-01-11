package com.pjh.product.application.port.out;

import com.pjh.product.application.port.dto.CatalogProductView;
import com.pjh.product.application.port.dto.CatalogSearchEntry;
import com.pjh.product.application.port.dto.CatalogSearchQuery;
import com.pjh.product.application.port.dto.CatalogSearchResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CatalogReadPort {

    Optional<CatalogProductView> fetchProductView(Long productId);

    CatalogSearchResponse fetchCatalogEntries(CatalogSearchQuery query);

    Map<Long, CatalogSearchEntry> fetchProductSummaries(List<Long> productIds);
}
