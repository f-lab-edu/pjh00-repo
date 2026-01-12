package com.pjh.product.adapters.out.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjh.product.adapters.out.redis.support.CatalogRedisKeyFactory;
import com.pjh.product.application.port.dto.CatalogProductView;
import com.pjh.product.application.port.dto.CatalogSearchEntry;
import com.pjh.product.application.port.dto.CatalogSearchQuery;
import com.pjh.product.application.port.dto.CatalogSearchQuery.SortOption;
import com.pjh.product.application.port.dto.CatalogSearchResponse;
import com.pjh.product.application.port.dto.InventorySummary;
import com.pjh.product.application.port.dto.PriceSummary;
import com.pjh.product.application.port.out.CatalogReadPort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisCatalogReadAdapter implements CatalogReadPort {

    private static final String DEFAULT_CURRENCY = "KRW";
    private static final TypeReference<List<String>> LIST_TYPE = new TypeReference<>() {};

    private final StringRedisTemplate redisTemplate;
    private final CatalogRedisKeyFactory keyFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisCatalogReadAdapter(StringRedisTemplate redisTemplate) {
        this(redisTemplate, new CatalogRedisKeyFactory(), new ObjectMapper());
    }

    RedisCatalogReadAdapter(StringRedisTemplate redisTemplate, CatalogRedisKeyFactory keyFactory, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.keyFactory = keyFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<CatalogProductView> fetchProductView(Long productId) {
        String key = keyFactory.productDetailKey(productId);
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Map<String, String> fields = hashOps.entries(key);
        if (fields == null || fields.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapToProductView(productId, fields));
    }

    @Override
    public CatalogSearchResponse fetchCatalogEntries(CatalogSearchQuery query) {
        String lookupKey = resolveLookupKey(query);
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Long total = lookupKey == null ? 0L : zSetOps.zCard(lookupKey);
        if (total == null || total <= 0 || lookupKey == null) {
            return new CatalogSearchResponse(Collections.emptyList(), 0L, query.page(), query.size());
        }

        long start = (long) query.page() * query.size();
        long end = start + query.size() - 1;
        SortOption sort = query.sortOption();
        Set<String> members = isAscending(sort)
                ? zSetOps.range(lookupKey, start, end)
                : zSetOps.reverseRange(lookupKey, start, end);
        if (members == null || members.isEmpty()) {
            return new CatalogSearchResponse(Collections.emptyList(), total, query.page(), query.size());
        }

        List<Long> productIds = members.stream()
                .map(this::safeParseLong)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        Map<Long, CatalogSearchEntry> summaries = fetchProductSummaries(productIds);
        List<CatalogSearchEntry> items = productIds.stream()
                .map(summaries::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<CatalogSearchEntry> filtered = applyFilters(items, query);
        return new CatalogSearchResponse(filtered, total, query.page(), query.size());
    }

    @Override
    public Map<Long, CatalogSearchEntry> fetchProductSummaries(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Map<Long, CatalogSearchEntry> result = new LinkedHashMap<>();
        for (Long productId : productIds) {
            if (productId == null) {
                continue;
            }
            Map<String, String> fields = hashOps.entries(keyFactory.productDetailKey(productId));
            if (fields == null || fields.isEmpty()) {
                continue;
            }
            result.put(productId, mapToSearchEntry(productId, fields));
        }
        return result;
    }

    private CatalogProductView mapToProductView(Long productId, Map<String, String> fields) {
        PriceSummary price = new PriceSummary(
                parseInt(fields.get("price:base"), 0),
                defaultCurrency(fields.get("price:currency")),
                parseNullableInt(fields.get("price:discounted"))
        );
        InventorySummary inventory = new InventorySummary(
                parseInt(fields.get("inventory:stock"), 0),
                parseBoolean(fields.get("inventory:in_stock"), true)
        );
        return new CatalogProductView(
                productId,
                parseLong(fields.get("brand:id")),
                fields.get("brand:name"),
                parseLong(fields.get("category:id")),
                fields.get("category:path"),
                fields.get("product:name"),
                fields.get("description:short"),
                price,
                inventory,
                parseList(fields.get("media:images")),
                fields.get("media:thumbnail"),
                parseList(fields.get("product:badges")),
                parseList(fields.get("product:highlights")),
                parseLong(fields.get("popularity:score"), 0L),
                parseLong(fields.get("timestamps:updated"), 0L)
        );
    }

    private CatalogSearchEntry mapToSearchEntry(Long productId, Map<String, String> fields) {
        PriceSummary price = new PriceSummary(
                parseInt(fields.get("price:base"), 0),
                defaultCurrency(fields.get("price:currency")),
                parseNullableInt(fields.get("price:discounted"))
        );
        return new CatalogSearchEntry(
                productId,
                parseLong(fields.get("brand:id")),
                fields.get("product:name"),
                fields.get("brand:name"),
                fields.get("category:path"),
                fields.get("media:thumbnail"),
                price,
                new InventorySummary(parseInt(fields.get("inventory:stock"), 0), parseBoolean(fields.get("inventory:in_stock"), true)),
                parseList(fields.get("product:badges")),
                parseLong(fields.get("popularity:score"), 0L)
        );
    }

    private List<CatalogSearchEntry> applyFilters(List<CatalogSearchEntry> entries, CatalogSearchQuery query) {
        if (entries.isEmpty()) {
            return entries;
        }
        return entries.stream()
                .filter(entry -> filterByBrand(entry, query))
                .filter(entry -> filterByPrice(entry, query))
                .collect(Collectors.toList());
    }

    private boolean filterByBrand(CatalogSearchEntry entry, CatalogSearchQuery query) {
        if (query.brandIds() == null || query.brandIds().isEmpty()) {
            return true;
        }
        return query.brandIds().contains(entry.brandId());
    }

    private boolean filterByPrice(CatalogSearchEntry entry, CatalogSearchQuery query) {
        Integer minPrice = query.minPrice();
        Integer maxPrice = query.maxPrice();
        int basePrice = entry.price().basePrice();
        if (minPrice != null && basePrice < minPrice) {
            return false;
        }
        if (maxPrice != null && basePrice > maxPrice) {
            return false;
        }
        return true;
    }

    private String resolveLookupKey(CatalogSearchQuery query) {
        SortOption sort = query.sortOption();
        if (query.keyword() != null && !query.keyword().isBlank()) {
            return keyFactory.keywordKey(query.keyword(), sort);
        }
        if (query.categoryId() != null && query.categoryId() > 0) {
            return keyFactory.categoryKey(query.categoryId(), sort);
        }
        return keyFactory.rankingKey(sort);
    }

    private boolean isAscending(SortOption sortOption) {
        SortOption option = sortOption == null ? SortOption.RELEVANCE : sortOption;
        return option == SortOption.PRICE_ASC;
    }

    private String defaultCurrency(String currency) {
        return (currency == null || currency.isBlank()) ? DEFAULT_CURRENCY : currency;
    }

    private List<String> parseList(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(raw, LIST_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.singletonList(raw.trim());
        }
    }

    private Integer parseNullableInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseInt(String raw, int defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Long parseLong(String raw) {
        return parseLong(raw, null);
    }

    private Long parseLong(String raw, Long defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Boolean parseBoolean(String raw, boolean defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(raw);
    }

    private Long safeParseLong(String raw) {
        try {
            return Long.valueOf(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
