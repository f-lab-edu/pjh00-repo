package com.pjh.product.adapters.out.persistence.entity;

import com.pjh.product.domain.model.Product;
import com.pjh.product.domain.model.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class ProductJpaEntity {

    @Id
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProductStatus status;

    @Column(name = "base_price", nullable = false)
    private int basePrice;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "popularity_score", nullable = false)
    private long popularityScore;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ProductJpaEntity() {
    }

    public static ProductJpaEntity from(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.id = product.getId();
        entity.sellerId = product.getSellerId();
        entity.brandId = product.getBrandId();
        entity.categoryId = product.getCategoryId();
        entity.name = product.getName();
        entity.description = product.getDescription();
        entity.status = product.getStatus();
        entity.basePrice = product.getBasePrice();
        entity.currency = product.getCurrency();
        entity.stockQuantity = product.getStockQuantity();
        entity.popularityScore = product.getPopularityScore();
        entity.deletedAt = product.getDeletedAt();
        entity.createdAt = product.getCreatedAt();
        entity.updatedAt = product.getUpdatedAt();
        return entity;
    }

    public Product toDomain() {
        return Product.of(
                id,
                sellerId,
                brandId,
                categoryId,
                name,
                description,
                status,
                basePrice,
                currency,
                stockQuantity,
                popularityScore,
                deletedAt,
                createdAt,
                updatedAt
        );
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

    public void applyDomain(Product product) {
        this.brandId = product.getBrandId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.basePrice = product.getBasePrice();
        this.stockQuantity = product.getStockQuantity();
        this.deletedAt = product.getDeletedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
