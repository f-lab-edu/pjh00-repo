package com.pjh.common.error;

public interface ErrorCode {
    String code();        // ex) ORDER-001
    int httpStatus();     // ex) 400
    String message();     // 기본 메시지
}
