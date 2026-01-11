package com.pjh.product.application.service;

import com.pjh.common.error.BusinessException;
import com.pjh.product.application.port.in.BrandCommandUseCase;
import com.pjh.product.application.port.out.BrandWritePort;
import com.pjh.product.application.port.out.IdGeneratorPort;
import com.pjh.product.domain.model.Brand;
import com.pjh.product.error.ProductErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BrandCommandService implements BrandCommandUseCase {

    private final BrandWritePort brandWritePort;
    private final IdGeneratorPort idGeneratorPort;

    public BrandCommandService(BrandWritePort brandWritePort, IdGeneratorPort idGeneratorPort) {
        this.brandWritePort = brandWritePort;
        this.idGeneratorPort = idGeneratorPort;
    }

    @Override
    public BrandResult createBrand(CreateBrandCommand command) {
        if (brandWritePort.existsByName(command.name())) {
            throw new BusinessException(ProductErrorCode.BRAND_DUPLICATED);
        }

        long brandId = command.id() != null ? command.id() : idGeneratorPort.generate();
        Brand brand = Brand.create(brandId, command.name());
        Brand saved = brandWritePort.save(brand);
        return new BrandResult(saved.getId(), saved.getName());
    }
}
