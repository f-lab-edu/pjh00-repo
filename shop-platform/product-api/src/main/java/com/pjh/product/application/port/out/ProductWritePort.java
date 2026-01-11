package com.pjh.product.application.port.out;

import com.pjh.product.domain.model.Product;

public interface ProductWritePort {

    Product save(Product product);

    Product update(Product product);

    void recordPriceHistory(Long historyId, Long productId, int oldPrice, int newPrice, String reason);

    void recordStockHistory(Long historyId, Long productId, int oldStock, int newStock, String reason);
}
