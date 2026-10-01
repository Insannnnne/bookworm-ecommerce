package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class CategoryDto {

    @Data
    public static class CategoryRequest {
        @NotBlank private String name;
        private String description;
    }

    @Data
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private Long productCount;
    }
}
