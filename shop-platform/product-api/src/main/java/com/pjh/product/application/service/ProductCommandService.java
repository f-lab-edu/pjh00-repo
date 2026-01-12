package com.pjh.product.application.service;

import com.pjh.common.error.BusinessException;
import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.common.util.Json;
import com.pjh.product.application.port.out.CatalogOutboxPort;
import com.pjh.product.application.port.out.IdGeneratorPort;
import com.pjh.product.application.port.out.ProductReadPort;
import com.pjh.product.application.port.out.ProductWritePort;
import com.pjh.product.domain.model.Category;
import com.pjh.product.domain.model.Product;
import com.pjh.product.domain.event.CatalogEventType;
import com.pjh.product.error.ProductErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductCommandService implements ProductCommandUseCase {

    private final ProductReadPort productReadPort;
    private final ProductWritePort productWritePort;
    private final IdGeneratorPort idGeneratorPort;
    private final CatalogOutboxPort catalogOutboxPort;

    public ProductCommandService(
            ProductReadPort productReadPort,
            ProductWritePort productWritePort,
            IdGeneratorPort idGeneratorPort,
            CatalogOutboxPort catalogOutboxPort
    ) {
        this.productReadPort = productReadPort;
        this.productWritePort = productWritePort;
        this.idGeneratorPort = idGeneratorPort;
        this.catalogOutboxPort = catalogOutboxPort;
    }

    @Override
    public ProductResult createProduct(CreateProductCommand command) {
        validateBrand(command.brandId());
        validateCategory(command.categoryId());

        Product product = Product.create(
                command.sellerId(),
                command.brandId(),
                command.categoryId(),
                command.name(),
                command.description(),
                command.status(),
                command.basePrice(),
                command.currency(),
                command.stockQuantity()
        );
        long productId = command.id() != null ? command.id() : idGeneratorPort.generate();
        product.assignId(productId);

        Product saved = productWritePort.save(product);
        appendProductSnapshotEvent(saved, CatalogEventType.PRODUCT_CREATED);
        return toResult(saved);
    }

    @Override
    public ProductResult updateProduct(Long productId, UpdateProductCommand command) {
        Product product = productReadPort.findActiveProduct(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        validateBrand(command.brandId());
        validateCategory(command.categoryId());

        product.updateBasicInfo(command.brandId(), command.categoryId(), command.name(), command.description(), command.status());
        Product updated = productWritePort.update(product);
        appendProductSnapshotEvent(updated, CatalogEventType.PRODUCT_UPDATED);
        return toResult(updated);
    }

    @Override
    public PriceChangeResult changePrice(Long productId, ChangePriceCommand command) {
        Product product = productReadPort.findActiveProduct(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        int oldPrice = product.getBasePrice();
        product.changePrice(command.newPrice());
        Product updated = productWritePort.update(product);

        long historyId = idGeneratorPort.generate();
        productWritePort.recordPriceHistory(historyId, productId, oldPrice, command.newPrice(), command.reason());
        appendPriceChangedEvent(updated, oldPrice);
        return new PriceChangeResult(updated.getId(), oldPrice, updated.getBasePrice());
    }

    @Override
    public StockChangeResult changeStock(Long productId, ChangeStockCommand command) {
        Product product = productReadPort.findActiveProduct(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        int oldStock = product.getStockQuantity();
        product.changeStock(command.newStock());
        Product updated = productWritePort.update(product);

        long historyId = idGeneratorPort.generate();
        productWritePort.recordStockHistory(historyId, productId, oldStock, command.newStock(), command.reason());
        appendStockChangedEvent(updated, oldStock);
        return new StockChangeResult(updated.getId(), oldStock, updated.getStockQuantity());
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productReadPort.findActiveProduct(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        product.softDelete(LocalDateTime.now());
        Product deleted = productWritePort.update(product);
        appendProductSnapshotEvent(deleted, CatalogEventType.PRODUCT_DELETED);
    }

    private void appendProductSnapshotEvent(Product product, CatalogEventType eventType) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", product.getId());
        payload.put("sellerId", product.getSellerId());
        payload.put("brandId", product.getBrandId());
        payload.put("categoryId", product.getCategoryId());
        payload.put("name", product.getName());
        payload.put("description", product.getDescription());
        payload.put("status", product.getStatus());
        payload.put("basePrice", product.getBasePrice());
        payload.put("currency", product.getCurrency());
        payload.put("stockQuantity", product.getStockQuantity());
        payload.put("deletedAt", formatDate(product.getDeletedAt()));
        payload.put("updatedAt", formatDate(product.getUpdatedAt()));
        payload.put("createdAt", formatDate(product.getCreatedAt()));
        writeOutbox(product.getId(), eventType, payload);
    }

    private void appendPriceChangedEvent(Product product, int oldPrice) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", product.getId());
        payload.put("oldPrice", oldPrice);
        payload.put("newPrice", product.getBasePrice());
        payload.put("currency", product.getCurrency());
        writeOutbox(product.getId(), CatalogEventType.PRICE_CHANGED, payload);
    }

    private void appendStockChangedEvent(Product product, int oldStock) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", product.getId());
        payload.put("oldStock", oldStock);
        payload.put("newStock", product.getStockQuantity());
        writeOutbox(product.getId(), CatalogEventType.STOCK_CHANGED, payload);
    }

    private void writeOutbox(Long productId, CatalogEventType eventType, Map<String, Object> payload) {
        long outboxId = idGeneratorPort.generate();
        catalogOutboxPort.save(new CatalogOutboxPort.CatalogOutboxMessage(
                outboxId,
                productId,
                eventType,
                Json.stringify(payload),
                System.currentTimeMillis()
        ));
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void validateBrand(Long brandId) {
        productReadPort.findActiveBrand(brandId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.BRAND_NOT_FOUND));
    }

    private void validateCategory(Long categoryId) {
        Category category = productReadPort.findActiveCategory(categoryId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.CATEGORY_NOT_FOUND));
        if (!category.isActive()) {
            throw new BusinessException(ProductErrorCode.CATEGORY_INACTIVE);
        }
    }

    private ProductResult toResult(Product product) {
        return new ProductResult(
                product.getId(),
                product.getSellerId(),
                product.getBrandId(),
                product.getCategoryId(),
                product.getName(),
                product.getDescription(),
                product.getStatus(),
                product.getBasePrice(),
                product.getCurrency(),
                product.getStockQuantity()
        );
    }
}
