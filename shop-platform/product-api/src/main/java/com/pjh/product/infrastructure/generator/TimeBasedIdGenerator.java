package com.pjh.product.infrastructure.generator;

import com.pjh.product.application.port.out.IdGeneratorPort;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class TimeBasedIdGenerator implements IdGeneratorPort {

    private final AtomicLong sequence = new AtomicLong(0L);
    private volatile long lastTimestamp = -1L;

    @Override
    public synchronized long generate() {
        long currentMillis = Instant.now().toEpochMilli();
        if (currentMillis == lastTimestamp) {
            return (currentMillis << 20) | (sequence.incrementAndGet() & ((1L << 20) - 1));
        }
        lastTimestamp = currentMillis;
        sequence.set(0L);
        return (currentMillis << 20);
    }
}
