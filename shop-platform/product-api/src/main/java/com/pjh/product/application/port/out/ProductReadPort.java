package com.pjh.product.application.port.out;

import com.pjh.product.domain.model.Brand;
import com.pjh.product.domain.model.Category;
import com.pjh.product.domain.model.Product;
import java.util.Optional;

public interface ProductReadPort {

    Optional<Product> findActiveProduct(Long productId);

    Optional<Brand> findActiveBrand(Long brandId);

    Optional<Category> findActiveCategory(Long categoryId);
}
