package com.pjh.common.time;

import java.time.Instant;

public class SystemClockHolder implements ClockHolder {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
