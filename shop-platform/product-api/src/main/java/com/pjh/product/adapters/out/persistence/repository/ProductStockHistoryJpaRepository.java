package com.pjh.product.adapters.out.persistence.repository;

import com.pjh.product.adapters.out.persistence.entity.ProductStockHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockHistoryJpaRepository extends JpaRepository<ProductStockHistoryJpaEntity, Long> {
}
