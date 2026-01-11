package com.pjh.product.adapters.out.persistence.repository;

import com.pjh.product.adapters.out.persistence.entity.ProductJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    Optional<ProductJpaEntity> findByIdAndDeletedAtIsNull(Long id);
}
