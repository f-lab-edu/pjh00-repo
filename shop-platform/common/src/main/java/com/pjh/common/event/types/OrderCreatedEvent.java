package com.pjh.common.event.types;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        String orderNo,
        List<Item> items
) {
    public record Item(
            Long productId,
            int quantity
    ) {}
}
