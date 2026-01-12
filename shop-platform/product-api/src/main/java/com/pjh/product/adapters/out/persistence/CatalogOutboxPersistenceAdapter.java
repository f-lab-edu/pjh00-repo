package com.pjh.product.adapters.out.persistence;

import com.pjh.product.adapters.out.persistence.entity.CatalogOutboxJpaEntity;
import com.pjh.product.adapters.out.persistence.repository.CatalogOutboxJpaRepository;
import com.pjh.product.application.port.out.CatalogOutboxPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CatalogOutboxPersistenceAdapter implements CatalogOutboxPort {

    private final CatalogOutboxJpaRepository catalogOutboxJpaRepository;

    public CatalogOutboxPersistenceAdapter(CatalogOutboxJpaRepository catalogOutboxJpaRepository) {
        this.catalogOutboxJpaRepository = catalogOutboxJpaRepository;
    }

    @Override
    public void save(CatalogOutboxMessage message) {
        CatalogOutboxJpaEntity entity = CatalogOutboxJpaEntity.from(message);
        catalogOutboxJpaRepository.save(entity);
    }
}
