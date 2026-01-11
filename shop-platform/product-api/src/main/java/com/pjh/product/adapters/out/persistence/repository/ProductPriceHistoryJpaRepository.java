package com.pjh.product.adapters.out.persistence.repository;

import com.pjh.product.adapters.out.persistence.entity.ProductPriceHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceHistoryJpaRepository extends JpaRepository<ProductPriceHistoryJpaEntity, Long> {
}
