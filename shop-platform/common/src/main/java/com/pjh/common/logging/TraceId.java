package com.pjh.common.logging;

import java.util.UUID;

public final class TraceId {
    private TraceId() {}

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
