package com.pjh.product.adapters.out.persistence.repository;

import com.pjh.product.adapters.out.persistence.entity.CatalogOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogOutboxJpaRepository extends JpaRepository<CatalogOutboxJpaEntity, Long> {
}
