package com.pjh.common.event;

import java.time.Instant;

public record EventEnvelope<T>(
        String eventId,
        String type,
        Instant occurredAt,
        T payload
) {}
