package com.pjh.product.adapters.out.redis;

import com.pjh.product.application.port.out.CatalogReadPort;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RedisCatalogReadAdapter implements CatalogReadPort {

    @Override
    public Object fetchProductView(Long productId) {
        throw new UnsupportedOperationException("Redis catalog read is not implemented yet");
    }

    @Override
    public List<Object> fetchCatalogEntries(String keyword) {
        throw new UnsupportedOperationException("Redis catalog read is not implemented yet");
    }
}
