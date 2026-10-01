package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthorDto {

    @Data
    public static class AuthorRequest {
        @NotBlank private String name;
        private String bio;
        private String profileImageUrl;
    }

    @Data
    public static class AuthorResponse {
        private Long id;
        private String name;
        private String bio;
        private String profileImageUrl;
    }
}
