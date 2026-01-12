package com.pjh.product.application.port.out;

import com.pjh.product.domain.event.CatalogEventType;
import java.util.Objects;

public interface CatalogOutboxPort {

    void save(CatalogOutboxMessage message);

    record CatalogOutboxMessage(
            Long id,
            Long productId,
            CatalogEventType eventType,
            String payload,
            long occurredAtEpochMillis
    ) {
        public CatalogOutboxMessage {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(productId, "productId");
            Objects.requireNonNull(eventType, "eventType");
            Objects.requireNonNull(payload, "payload");
        }
    }
}
