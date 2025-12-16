package com.pjh.common.observability;

public final class MetricNames {
    private MetricNames() {}

    // HTTP
    public static final String HTTP_SERVER_REQUESTS = "http.server.requests";

    // Order
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CONFIRMED = "order.confirmed";

    // Product
    public static final String PRODUCT_CREATED = "product.created";
    public static final String PRODUCT_UPDATED = "product.updated";

    // Stock
    public static final String STOCK_DECREASED = "stock.decreased";
    public static final String STOCK_DECREASE_FAILED = "stock.decrease.failed";
}
