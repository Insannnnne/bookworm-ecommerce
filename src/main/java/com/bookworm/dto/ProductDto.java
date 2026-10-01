package com.bookworm.dto;

import com.bookworm.entity.Product;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductDto {

    @Data
    public static class ProductRequest {
        @NotBlank private String title;
        @NotNull private Long authorId;
        private String publisher;
        @NotEmpty private List<Long> categoryIds;
        private String description;
        @NotNull @DecimalMin("0.0") private BigDecimal price;
        @NotNull private Product.Format format;
        @NotBlank private String language;
        private String coverImageUrl;
        private String isbn;
        private LocalDate publishedDate;
        private Integer stock = 0;
        private Boolean featured = false;
        private Boolean newLaunch = false;
        private Boolean bestseller = false;
    }

    @Data
    public static class ProductSummary {
        private Long id;
        private String title;
        private String authorName;
        private String coverImageUrl;
        private BigDecimal price;
        private String format;
        private List<String> categories;
        private Double averageRating;
        private LocalDate deliveryDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ProductResponse extends ProductSummary {
        private String language;
        private String isbn;
        private LocalDate publishedDate;
        private Integer stock;
        private Integer soldCount;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ProductDetailResponse extends ProductResponse {
        private String description;
        private String publisher;
        private AuthorDto.AuthorResponse author;
        private Integer reviewCount;
    }
}
