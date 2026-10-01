package com.bookworm.controller;

import com.bookworm.dto.CategoryDto;
import com.bookworm.dto.ProductDto;
import com.bookworm.service.CategoryService;
import com.bookworm.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CategoryDto.CategoryResponse>> listAll() {
        return ResponseEntity.ok(categoryService.listAll());
    }

    @PostMapping
    public ResponseEntity<CategoryDto.CategoryResponse> create(
            @Valid @RequestBody CategoryDto.CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto.CategoryResponse> getById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getById(categoryId));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto.CategoryResponse> update(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDto.CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Page<ProductDto.ProductSummary>> getProducts(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getByCategory(categoryId, page, size));
    }
}
