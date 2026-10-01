package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AddressDto {

    @Data
    public static class AddressRequest {
        @NotBlank private String firstName;
        @NotBlank private String lastName;
        @NotBlank private String addressLine1;
        private String addressLine2;
        @NotBlank private String city;
        @NotBlank private String pinCode;
        @NotBlank private String state;
        private String country = "India";
        @NotBlank private String phone;
    }

    @Data
    public static class AddressResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String pinCode;
        private String state;
        private String country;
        private String phone;
    }
}
