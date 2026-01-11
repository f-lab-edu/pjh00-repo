package com.pjh.product.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_price_history")
public class ProductPriceHistoryJpaEntity {

    @Id
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "old_price", nullable = false)
    private int oldPrice;

    @Column(name = "new_price", nullable = false)
    private int newPrice;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ProductPriceHistoryJpaEntity() {
    }

    private ProductPriceHistoryJpaEntity(Long id, Long productId, int oldPrice, int newPrice, String reason) {
        this.id = id;
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.reason = reason;
    }

    public static ProductPriceHistoryJpaEntity of(Long id, Long productId, int oldPrice, int newPrice, String reason) {
        return new ProductPriceHistoryJpaEntity(id, productId, oldPrice, newPrice, reason);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
