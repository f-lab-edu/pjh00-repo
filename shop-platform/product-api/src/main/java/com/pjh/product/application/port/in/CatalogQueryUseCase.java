package com.pjh.product.application.port.in;

import com.pjh.product.application.port.dto.CatalogProductView;
import com.pjh.product.application.port.dto.CatalogSearchQuery;
import com.pjh.product.application.port.dto.CatalogSearchResponse;

public interface CatalogQueryUseCase {

    CatalogProductView getProductDetail(Long productId);

    CatalogSearchResponse searchCatalog(CatalogSearchQuery query);
}
