package com.pjh.product.adapters.out.persistence;

import com.pjh.product.adapters.out.persistence.entity.BrandJpaEntity;
import com.pjh.product.adapters.out.persistence.repository.BrandJpaRepository;
import com.pjh.product.application.port.out.BrandWritePort;
import com.pjh.product.domain.model.Brand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class BrandPersistenceAdapter implements BrandWritePort {

    private final BrandJpaRepository brandJpaRepository;

    public BrandPersistenceAdapter(BrandJpaRepository brandJpaRepository) {
        this.brandJpaRepository = brandJpaRepository;
    }

    @Override
    public boolean existsByName(String name) {
        return brandJpaRepository.existsByNameAndDeletedAtIsNull(name);
    }

    @Override
    public Brand save(Brand brand) {
        BrandJpaEntity saved = brandJpaRepository.save(BrandJpaEntity.from(brand));
        return saved.toDomain();
    }
}
