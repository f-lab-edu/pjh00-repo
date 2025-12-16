package com.pjh.common.util;

import java.util.UUID;

public final class Ids {
    private Ids() {}

    /** 이벤트/요청/엔티티 공통 UUID */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
