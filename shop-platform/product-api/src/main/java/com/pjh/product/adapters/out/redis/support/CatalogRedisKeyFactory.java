package com.pjh.product.adapters.out.redis.support;

import com.pjh.product.application.port.dto.CatalogSearchQuery.SortOption;
import java.util.Locale;
import java.util.regex.Pattern;

public final class CatalogRedisKeyFactory {

    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    public String productDetailKey(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId must be positive");
        }
        return "catalog:product:" + productId;
    }

    public String categoryKey(Long categoryId, SortOption sortOption) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("categoryId must be positive");
        }
        return "catalog:category:" + categoryId + ":" + sortSuffix(sortOption);
    }

    public String keywordKey(String keyword, SortOption sortOption) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }
        return "catalog:kw:" + normalizeKeyword(keyword) + ":" + sortSuffix(sortOption);
    }

    public String rankingKey(SortOption sortOption) {
        return "catalog:ranking:" + switch (sortOrDefault(sortOption)) {
            case PRICE_ASC -> "price:asc";
            case PRICE_DESC -> "price:desc";
            case NEWEST -> "newest";
            case POPULARITY -> "popular";
            case RELEVANCE -> "relevance";
        };
    }

    private String sortSuffix(SortOption sortOption) {
        return switch (sortOrDefault(sortOption)) {
            case PRICE_ASC -> "price:asc";
            case PRICE_DESC -> "price:desc";
            case NEWEST -> "new";
            case POPULARITY -> "popular";
            case RELEVANCE -> "relevance";
        };
    }

    private SortOption sortOrDefault(SortOption sortOption) {
        return sortOption == null ? SortOption.RELEVANCE : sortOption;
    }

    private String normalizeKeyword(String keyword) {
        String lowered = keyword.toLowerCase(Locale.KOREA).trim();
        return MULTIPLE_SPACES.matcher(lowered).replaceAll(":");
    }
}
