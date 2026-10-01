package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

public class ReviewDto {

    @Data
    public static class ReviewRequest {
        @NotNull @Min(1) @Max(5) private Integer rating;
        @NotBlank @Size(max = 500) private String comment;
    }

    @Data
    public static class ReviewResponse {
        private Long id;
        private Long userId;
        private String reviewerName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}
