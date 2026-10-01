package com.bookworm.controller;

import com.bookworm.dto.ProductDto;
import com.bookworm.dto.ReviewDto;
import com.bookworm.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto.ProductSummary>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "relevance") String sort) {
        return ResponseEntity.ok(productService.listAll(page, size, sort));
    }

    @PostMapping
    public ResponseEntity<ProductDto.ProductResponse> create(
            @Valid @RequestBody ProductDto.ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto.ProductSummary>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.search(
                q, categoryId, authorId, language, format, minPrice, maxPrice, sort, page, size));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductDto.ProductSummary>> getFeatured(
            @RequestParam String section,
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(productService.getFeatured(section, limit));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductDetailResponse> getById(
            @PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductResponse> update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDto.ProductRequest request) {
        return ResponseEntity.ok(productService.update(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/related")
    public ResponseEntity<List<ProductDto.ProductSummary>> getRelated(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(productService.getRelated(productId, limit));
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<Page<ReviewDto.ReviewResponse>> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getReviews(productId, page, size));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ReviewDto.ReviewResponse> addReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewDto.ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addReview(productId, userDetails.getUsername(), request));
    }
}
