package com.pjh.product.adapters.out.persistence.repository;

import com.pjh.product.adapters.out.persistence.entity.CategoryJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    Optional<CategoryJpaEntity> findByIdAndDeletedAtIsNull(Long id);
}
