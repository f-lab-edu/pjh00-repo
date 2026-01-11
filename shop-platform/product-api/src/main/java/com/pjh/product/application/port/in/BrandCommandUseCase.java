package com.pjh.product.application.port.in;

public interface BrandCommandUseCase {

    BrandResult createBrand(CreateBrandCommand command);

    record CreateBrandCommand(Long id, String name) {
    }

    record BrandResult(Long id, String name) {
    }
}
