package com.pjh.common.util;

public final class Preconditions {

    private Preconditions() {}

    public static void require(boolean condition, RuntimeException ex) {
        if (!condition) {
            throw ex;
        }
    }

    public static void requireNotNull(Object value, RuntimeException ex) {
        if (value == null) {
            throw ex;
        }
    }
}
