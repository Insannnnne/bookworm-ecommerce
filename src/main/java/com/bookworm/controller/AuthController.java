package com.bookworm.controller;

import com.bookworm.dto.AuthDto;
import com.bookworm.dto.UserDto;
import com.bookworm.dto.AddressDto;
import com.bookworm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<AuthDto.AuthResponse> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthDto.AuthResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        // JWT is stateless; client discards the token
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserDto.UserResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PutMapping("/users/me")
    public ResponseEntity<UserDto.UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), request));
    }

    @GetMapping("/users/me/addresses")
    public ResponseEntity<List<AddressDto.AddressResponse>> listAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.listAddresses(userDetails.getUsername()));
    }

    @PostMapping("/users/me/addresses")
    public ResponseEntity<AddressDto.AddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressDto.AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addAddress(userDetails.getUsername(), request));
    }

    @PutMapping("/users/me/addresses/{addressId}")
    public ResponseEntity<AddressDto.AddressResponse> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDto.AddressRequest request) {
        return ResponseEntity.ok(
                userService.updateAddress(userDetails.getUsername(), addressId, request));
    }

    @DeleteMapping("/users/me/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId) {
        userService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.noContent().build();
    }
}
