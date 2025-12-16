package com.pjh.common.util;

public final class Strings {
    private Strings() {}

    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
