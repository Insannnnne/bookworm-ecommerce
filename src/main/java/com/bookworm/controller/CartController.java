package com.bookworm.controller;

import com.bookworm.dto.CartDto;
import com.bookworm.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto.CartResponse> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto.CartResponse> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartDto.CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userDetails.getUsername(), request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto.CartResponse> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartDto.CartItemUpdateRequest request) {
        return ResponseEntity.ok(cartService.updateItem(userDetails.getUsername(), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto.CartResponse> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUsername(), cartItemId));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CartDto.CartResponse> applyCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartDto.CouponRequest request) {
        return ResponseEntity.ok(cartService.applyCoupon(userDetails.getUsername(), request));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartDto.CartResponse> removeCoupon(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeCoupon(userDetails.getUsername()));
    }
}
