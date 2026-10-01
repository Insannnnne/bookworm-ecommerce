package com.bookworm.controller;

import com.bookworm.dto.ProductDto;
import com.bookworm.dto.WishlistDto;
import com.bookworm.service.ProductService;
import com.bookworm.service.UserService;
import com.bookworm.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserFeatureController {

    private final WishlistService wishlistService;
    private final ProductService productService;
    private final UserService userService;

    // ── Wishlist ──────────────────────────────────────────────────────────
    @GetMapping("/wishlist")
    public ResponseEntity<List<WishlistDto.WishlistItemResponse>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/wishlist")
    public ResponseEntity<WishlistDto.WishlistItemResponse> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WishlistDto.WishlistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.addToWishlist(userDetails.getUsername(), request));
    }

    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }

    // ── Recommendations ───────────────────────────────────────────────────
    @GetMapping("/recommendations")
    public ResponseEntity<List<ProductDto.ProductSummary>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(productService.getRecommendationsForUser(userId, limit));
    }
}
