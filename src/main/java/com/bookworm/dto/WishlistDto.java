package com.bookworm.dto;

import lombok.Data;
import java.time.LocalDateTime;

public class WishlistDto {

    @Data
    public static class WishlistRequest {
        @jakarta.validation.constraints.NotNull
        private Long productId;
    }

    @Data
    public static class WishlistItemResponse {
        private Long id;
        private ProductDto.ProductSummary product;
        private LocalDateTime addedAt;
    }
}
