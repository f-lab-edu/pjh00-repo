package com.pjh.product.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Category {

    private final Long id;
    private final Long parentId;
    private final String name;
    private final int depth;
    private final boolean active;
    private final LocalDateTime deletedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Category(
            Long id,
            Long parentId,
            String name,
            int depth,
            boolean active,
            LocalDateTime deletedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.parentId = parentId;
        this.name = Objects.requireNonNull(name, "name");
        this.depth = depth;
        this.active = active;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Category of(
            Long id,
            Long parentId,
            String name,
            int depth,
            boolean active,
            LocalDateTime deletedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Category(id, parentId, name, depth, active, deletedAt, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
