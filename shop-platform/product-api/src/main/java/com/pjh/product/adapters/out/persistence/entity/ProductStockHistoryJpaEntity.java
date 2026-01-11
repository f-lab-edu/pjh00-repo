package com.pjh.product.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_stock_history")
public class ProductStockHistoryJpaEntity {

    @Id
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "old_stock", nullable = false)
    private int oldStock;

    @Column(name = "new_stock", nullable = false)
    private int newStock;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ProductStockHistoryJpaEntity() {
    }

    private ProductStockHistoryJpaEntity(Long id, Long productId, int oldStock, int newStock, String reason) {
        this.id = id;
        this.productId = productId;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.reason = reason;
    }

    public static ProductStockHistoryJpaEntity of(Long id, Long productId, int oldStock, int newStock, String reason) {
        return new ProductStockHistoryJpaEntity(id, productId, oldStock, newStock, reason);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
