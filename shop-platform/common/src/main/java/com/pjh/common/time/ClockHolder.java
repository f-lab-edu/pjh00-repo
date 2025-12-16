package com.pjh.common.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public interface ClockHolder {

    Instant now();

    default LocalDateTime nowLocal() {
        return LocalDateTime.ofInstant(now(), ZoneId.systemDefault());
    }
}
