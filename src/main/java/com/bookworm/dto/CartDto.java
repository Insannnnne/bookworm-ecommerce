package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartDto {

    @Data
    public static class CartItemRequest {
        @NotNull private Long productId;
        @NotNull @Min(1) private Integer quantity;
    }

    @Data
    public static class CartItemUpdateRequest {
        @NotNull @Min(1) private Integer quantity;
    }

    @Data
    public static class CouponRequest {
        @NotBlank private String code;
    }

    @Data
    public static class CartItemResponse {
        private Long id;
        private ProductDto.ProductSummary product;
        private Integer quantity;
        private BigDecimal lineTotal;
    }

    @Data
    public static class CartResponse {
        private Long id;
        private java.util.List<CartItemResponse> items;
        private BigDecimal subTotal;
        private BigDecimal tax;
        private BigDecimal deliveryCharges;
        private String couponCode;
        private BigDecimal discount;
        private BigDecimal totalAmount;
    }
}
