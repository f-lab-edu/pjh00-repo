package com.pjh.common.error;

public record ErrorResponse(
        String code,
        String message,
        String traceId
) {
    public static ErrorResponse of(ErrorCode code, String message, String traceId) {
        return new ErrorResponse(code.code(), message, traceId);
    }
}
