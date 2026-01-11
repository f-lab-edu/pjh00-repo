package com.pjh.product.adapters.in.web;

import com.pjh.common.api.ApiResponse;
import com.pjh.product.adapters.in.web.dto.BrandResponse;
import com.pjh.product.adapters.in.web.dto.CreateBrandRequest;
import com.pjh.product.application.port.in.BrandCommandUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/brands")
public class AdminBrandController {

    private final BrandCommandUseCase brandCommandUseCase;

    public AdminBrandController(BrandCommandUseCase brandCommandUseCase) {
        this.brandCommandUseCase = brandCommandUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BrandResponse> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        var result = brandCommandUseCase.createBrand(request.toCommand());
        return ApiResponse.success(BrandResponse.from(result));
    }
}
