package com.pjh.product.adapters.in.web;

import com.pjh.common.api.ApiResponse;
import com.pjh.product.adapters.in.web.dto.ChangePriceRequest;
import com.pjh.product.adapters.in.web.dto.ChangeStockRequest;
import com.pjh.product.adapters.in.web.dto.CreateProductRequest;
import com.pjh.product.adapters.in.web.dto.PriceChangeResponse;
import com.pjh.product.adapters.in.web.dto.ProductResponse;
import com.pjh.product.adapters.in.web.dto.StockChangeResponse;
import com.pjh.product.adapters.in.web.dto.UpdateProductRequest;
import com.pjh.product.application.port.in.ProductCommandUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductCommandUseCase productCommandUseCase;

    public AdminProductController(ProductCommandUseCase productCommandUseCase) {
        this.productCommandUseCase = productCommandUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        var result = productCommandUseCase.createProduct(request.toCommand());
        return ApiResponse.success(ProductResponse.from(result));
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        var result = productCommandUseCase.updateProduct(productId, request.toCommand());
        return ApiResponse.success(ProductResponse.from(result));
    }

    @PatchMapping("/{productId}/price")
    public ApiResponse<PriceChangeResponse> changePrice(
            @PathVariable Long productId,
            @Valid @RequestBody ChangePriceRequest request
    ) {
        var result = productCommandUseCase.changePrice(productId, request.toCommand());
        return ApiResponse.success(PriceChangeResponse.from(result));
    }

    @PatchMapping("/{productId}/stock")
    public ApiResponse<StockChangeResponse> changeStock(
            @PathVariable Long productId,
            @Valid @RequestBody ChangeStockRequest request
    ) {
        var result = productCommandUseCase.changeStock(productId, request.toCommand());
        return ApiResponse.success(StockChangeResponse.from(result));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        productCommandUseCase.deleteProduct(productId);
        return ApiResponse.success(null);
    }
}
