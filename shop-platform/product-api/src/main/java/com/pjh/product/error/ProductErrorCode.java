package com.pjh.product.error;

import com.pjh.common.error.ErrorCode;

public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("PRODUCT-404-001", 404, "Product not found"),
    BRAND_NOT_FOUND("PRODUCT-404-002", 404, "Brand not found"),
    BRAND_DUPLICATED("PRODUCT-409-001", 409, "Brand name already exists"),
    CATEGORY_NOT_FOUND("PRODUCT-404-003", 404, "Category not found"),
    CATEGORY_INACTIVE("PRODUCT-400-001", 400, "Category is inactive"),
    INVALID_INPUT("PRODUCT-400-002", 400, "Invalid request"),
    INTERNAL_ERROR("PRODUCT-500-001", 500, "Internal server error");

    private final String code;
    private final int httpStatus;
    private final String message;

    ProductErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String message() {
        return message;
    }
}
