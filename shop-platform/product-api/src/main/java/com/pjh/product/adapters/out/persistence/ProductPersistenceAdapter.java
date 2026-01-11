package com.pjh.product.adapters.out.persistence;

import com.pjh.product.adapters.out.persistence.entity.ProductJpaEntity;
import com.pjh.product.adapters.out.persistence.entity.ProductPriceHistoryJpaEntity;
import com.pjh.product.adapters.out.persistence.entity.ProductStockHistoryJpaEntity;
import com.pjh.product.adapters.out.persistence.repository.BrandJpaRepository;
import com.pjh.product.adapters.out.persistence.repository.CategoryJpaRepository;
import com.pjh.product.adapters.out.persistence.repository.ProductJpaRepository;
import com.pjh.product.adapters.out.persistence.repository.ProductPriceHistoryJpaRepository;
import com.pjh.product.adapters.out.persistence.repository.ProductStockHistoryJpaRepository;
import com.pjh.product.application.port.out.ProductReadPort;
import com.pjh.product.application.port.out.ProductWritePort;
import com.pjh.product.domain.model.Brand;
import com.pjh.product.domain.model.Category;
import com.pjh.product.domain.model.Product;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProductPersistenceAdapter implements ProductReadPort, ProductWritePort {

    private final ProductJpaRepository productJpaRepository;
    private final BrandJpaRepository brandJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductPriceHistoryJpaRepository priceHistoryJpaRepository;
    private final ProductStockHistoryJpaRepository stockHistoryJpaRepository;

    public ProductPersistenceAdapter(
            ProductJpaRepository productJpaRepository,
            BrandJpaRepository brandJpaRepository,
            CategoryJpaRepository categoryJpaRepository,
            ProductPriceHistoryJpaRepository priceHistoryJpaRepository,
            ProductStockHistoryJpaRepository stockHistoryJpaRepository
    ) {
        this.productJpaRepository = productJpaRepository;
        this.brandJpaRepository = brandJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.priceHistoryJpaRepository = priceHistoryJpaRepository;
        this.stockHistoryJpaRepository = stockHistoryJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findActiveProduct(Long productId) {
        return productJpaRepository.findByIdAndDeletedAtIsNull(productId).map(ProductJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Brand> findActiveBrand(Long brandId) {
        return brandJpaRepository.findByIdAndDeletedAtIsNull(brandId).map(entity -> entity.toDomain());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findActiveCategory(Long categoryId) {
        return categoryJpaRepository.findByIdAndDeletedAtIsNull(categoryId).map(entity -> entity.toDomain());
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.from(product);
        ProductJpaEntity saved = productJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Product update(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.from(product);
        ProductJpaEntity saved = productJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void recordPriceHistory(Long historyId, Long productId, int oldPrice, int newPrice, String reason) {
        ProductPriceHistoryJpaEntity entity = ProductPriceHistoryJpaEntity.of(historyId, productId, oldPrice, newPrice, reason);
        priceHistoryJpaRepository.save(entity);
    }

    @Override
    public void recordStockHistory(Long historyId, Long productId, int oldStock, int newStock, String reason) {
        ProductStockHistoryJpaEntity entity = ProductStockHistoryJpaEntity.of(historyId, productId, oldStock, newStock, reason);
        stockHistoryJpaRepository.save(entity);
    }

}
