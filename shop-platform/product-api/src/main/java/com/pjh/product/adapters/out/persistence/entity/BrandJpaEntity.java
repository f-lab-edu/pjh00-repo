package com.pjh.product.adapters.out.persistence.entity;

import com.pjh.product.domain.model.Brand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "brand")
public class BrandJpaEntity {

    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected BrandJpaEntity() {
    }

    public static BrandJpaEntity from(Brand brand) {
        BrandJpaEntity entity = new BrandJpaEntity();
        entity.id = brand.getId();
        entity.name = brand.getName();
        entity.deletedAt = brand.getDeletedAt();
        entity.createdAt = brand.getCreatedAt();
        entity.updatedAt = brand.getUpdatedAt();
        return entity;
    }

    public Brand toDomain() {
        return Brand.of(id, name, deletedAt, createdAt, updatedAt);
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
