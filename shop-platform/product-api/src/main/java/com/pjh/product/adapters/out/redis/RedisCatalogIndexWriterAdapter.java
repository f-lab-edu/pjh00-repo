package com.pjh.product.adapters.out.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisCatalogIndexWriterAdapter {

    public void writeIndex(Object payload) {
        throw new UnsupportedOperationException("Redis catalog indexing is not implemented yet");
    }
}
