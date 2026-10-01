package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

public class UserDto {

    @Data
    public static class UserResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank private String firstName;
        @NotBlank private String lastName;
    }
}
