package com.pjh.product.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Brand {

    private final Long id;
    private final String name;
    private final LocalDateTime deletedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Brand(Long id, String name, LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Brand create(Long id, String name) {
        return new Brand(id, name, null, null, null);
    }

    public static Brand of(Long id, String name, LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Brand(id, name, deletedAt, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
