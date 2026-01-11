package com.pjh.product.adapters.out.redis;

import com.pjh.product.application.port.dto.CatalogProductView;
import com.pjh.product.application.port.dto.CatalogSearchEntry;
import com.pjh.product.application.port.dto.CatalogSearchQuery;
import com.pjh.product.application.port.dto.CatalogSearchResponse;
import com.pjh.product.application.port.out.CatalogReadPort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RedisCatalogReadAdapter implements CatalogReadPort {

    @Override
    public Optional<CatalogProductView> fetchProductView(Long productId) {
        throw new UnsupportedOperationException("Redis catalog read is not implemented yet");
    }

    @Override
    public CatalogSearchResponse fetchCatalogEntries(CatalogSearchQuery query) {
        throw new UnsupportedOperationException("Redis catalog read is not implemented yet");
    }

    @Override
    public Map<Long, CatalogSearchEntry> fetchProductSummaries(List<Long> productIds) {
        throw new UnsupportedOperationException("Redis catalog read is not implemented yet");
    }
}
