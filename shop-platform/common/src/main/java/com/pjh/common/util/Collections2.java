package com.pjh.common.util;

import java.util.Collection;

public final class Collections2 {
    private Collections2() {}

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean hasElements(Collection<?> c) {
        return c != null && !c.isEmpty();
    }
}
