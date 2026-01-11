package com.pjh.product.application.port.out;

import com.pjh.product.domain.model.Brand;

public interface BrandWritePort {

    boolean existsByName(String name);

    Brand save(Brand brand);
}
