package com.pjh.product.application.port.in;

import java.util.List;

public interface CatalogQueryUseCase {

    // TODO: implement when Redis catalog feature branch is ready
    Object getProductDetail(Long productId);

    List<Object> searchCatalog(String keyword);
}
