package com.pjh.product.adapters.in.web;

import com.pjh.common.api.ApiResponse;
import com.pjh.common.error.BusinessException;
import com.pjh.common.error.ErrorResponse;
import com.pjh.common.logging.TraceContextHolder;
import com.pjh.product.error.ProductErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {AdminProductController.class, AdminBrandController.class})
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        var error = ErrorResponse.of(ex.getErrorCode(), ex.getMessage(), TraceContextHolder.getOrGenerate());
        log.warn("Business exception handled. code={}, message={}"
                , ex.getErrorCode().code(), ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode().httpStatus()).body(ApiResponse.fail(error));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException manv && manv.getBindingResult().hasErrors()) {
            message = manv.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else if (ex instanceof BindException bind && bind.getBindingResult().hasErrors()) {
            message = bind.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        log.warn("Validation/illegal argument error. code={}, message={}", ProductErrorCode.INVALID_INPUT.code(), message);
        var error = ErrorResponse.of(ProductErrorCode.INVALID_INPUT, message, TraceContextHolder.getOrGenerate());
        return ResponseEntity.status(ProductErrorCode.INVALID_INPUT.httpStatus()).body(ApiResponse.fail(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception ex) {
        log.error("Unexpected server error. code={}", ProductErrorCode.INTERNAL_ERROR.code(), ex);
        var error = ErrorResponse.of(ProductErrorCode.INTERNAL_ERROR, "Unexpected server error", TraceContextHolder.getOrGenerate());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail(error));
    }
}
