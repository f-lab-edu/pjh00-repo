package com.pjh.product.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Product {

    private Long id;
    private final Long sellerId;
    private Long brandId;
    private Long categoryId;
    private String name;
    private String description;
    private ProductStatus status;
    private int basePrice;
    private final String currency;
    private int stockQuantity;
    private long popularityScore;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Product(
            Long id,
            Long sellerId,
            Long brandId,
            Long categoryId,
            String name,
            String description,
            ProductStatus status,
            int basePrice,
            String currency,
            int stockQuantity,
            long popularityScore,
            LocalDateTime deletedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.sellerId = requirePositive(sellerId, "sellerId");
        this.brandId = requirePositive(brandId, "brandId");
        this.categoryId = requirePositive(categoryId, "categoryId");
        this.name = requireText(name, "name");
        this.description = description;
        this.status = Objects.requireNonNull(status, "status");
        this.basePrice = requireNonNegative(basePrice, "basePrice");
        this.currency = requireText(currency, "currency");
        this.stockQuantity = requireNonNegative(stockQuantity, "stockQuantity");
        this.popularityScore = popularityScore;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Product create(
            Long sellerId,
            Long brandId,
            Long categoryId,
            String name,
            String description,
            ProductStatus status,
            int basePrice,
            String currency,
            int stockQuantity
    ) {
        return new Product(null, sellerId, brandId, categoryId, name, description, status, basePrice, currency, stockQuantity, 0L, null, null, null);
    }

    public static Product of(
            Long id,
            Long sellerId,
            Long brandId,
            Long categoryId,
            String name,
            String description,
            ProductStatus status,
            int basePrice,
            String currency,
            int stockQuantity,
            long popularityScore,
            LocalDateTime deletedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Product(id, sellerId, brandId, categoryId, name, description, status, basePrice, currency, stockQuantity, popularityScore, deletedAt, createdAt, updatedAt);
    }

    public void assignId(Long id) {
        this.id = requirePositive(id, "id");
    }

    public void updateBasicInfo(Long brandId, Long categoryId, String name, String description, ProductStatus status) {
        this.brandId = requirePositive(brandId, "brandId");
        this.categoryId = requirePositive(categoryId, "categoryId");
        this.name = requireText(name, "name");
        this.description = description;
        this.status = Objects.requireNonNull(status, "status");
    }

    public void changePrice(int newPrice) {
        this.basePrice = requireNonNegative(newPrice, "basePrice");
    }

    public void changeStock(int newStock) {
        this.stockQuantity = requireNonNegative(newStock, "stockQuantity");
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.deletedAt = Objects.requireNonNull(deletedAt, "deletedAt");
    }

    private static Long requirePositive(Long value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    private static int requireNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must be greater or equal to zero");
        }
        return value;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public long getPopularityScore() {
        return popularityScore;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
