package com.pjh.product.adapters.out.persistence.entity;

import com.pjh.product.domain.model.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "category")
public class CategoryJpaEntity {

    @Id
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "depth", nullable = false)
    private int depth;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CategoryJpaEntity() {
    }

    public Category toDomain() {
        return Category.of(id, parentId, name, depth, active, deletedAt, createdAt, updatedAt);
    }
}
