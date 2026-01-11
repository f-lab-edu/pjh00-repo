package com.pjh.common.logging;

public final class TraceContextHolder {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private TraceContextHolder() {
    }

    public static void set(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static void setIfAbsent(String traceId) {
        if (TRACE_ID.get() == null && traceId != null && !traceId.isBlank()) {
            TRACE_ID.set(traceId);
        }
    }

    public static String get() {
        return TRACE_ID.get();
    }

    public static String getOrGenerate() {
        String current = TRACE_ID.get();
        if (current == null || current.isBlank()) {
            current = TraceId.generate();
            TRACE_ID.set(current);
        }
        return current;
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
