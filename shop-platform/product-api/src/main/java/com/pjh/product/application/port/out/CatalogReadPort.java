package com.pjh.product.application.port.out;

import java.util.List;

public interface CatalogReadPort {

    // TODO: implement with Redis catalog module
    Object fetchProductView(Long productId);

    List<Object> fetchCatalogEntries(String keyword);
}
