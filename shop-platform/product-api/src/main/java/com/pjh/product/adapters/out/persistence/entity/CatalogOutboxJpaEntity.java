package com.pjh.product.adapters.out.persistence.entity;

import com.pjh.product.application.port.out.CatalogOutboxPort.CatalogOutboxMessage;
import com.pjh.product.domain.event.CatalogEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "product_catalog_outbox")
public class CatalogOutboxJpaEntity {

    @Id
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private CatalogEventType eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "json")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected CatalogOutboxJpaEntity() {
    }

    public static CatalogOutboxJpaEntity from(CatalogOutboxMessage message) {
        CatalogOutboxJpaEntity entity = new CatalogOutboxJpaEntity();
        entity.id = message.id();
        entity.productId = message.productId();
        entity.eventType = message.eventType();
        entity.payload = message.payload();
        entity.occurredAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.occurredAtEpochMillis()), ZoneOffset.UTC);
        entity.processed = false;
        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
