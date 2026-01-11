package com.pjh.product.application.service;

import com.pjh.common.error.BusinessException;
import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.product.application.port.out.IdGeneratorPort;
import com.pjh.product.application.port.out.ProductReadPort;
import com.pjh.product.application.port.out.ProductWritePort;
import com.pjh.product.domain.model.Category;
import com.pjh.product.domain.model.Product;
import com.pjh.product.error.ProductErrorCode;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductCommandService implements ProductCommandUseCase {

    private final ProductReadPort productReadPort;
    private final ProductWritePort productWritePort;
    private final IdGeneratorPort idGeneratorPort;

    public ProductCommandService(
            ProductReadPort productReadPort,
            ProductWritePort productWritePort,
            IdGeneratorPort idGeneratorPort
    ) {
        this.productReadPort = productReadPort;
        this.productWritePort = productWritePort;
        this.idGeneratorPort = idGeneratorPort;
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
        return new StockChangeResult(updated.getId(), oldStock, updated.getStockQuantity());
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productReadPort.findActiveProduct(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        product.softDelete(LocalDateTime.now());
        productWritePort.update(product);
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
