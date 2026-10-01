package com.bookworm.controller;

import com.bookworm.dto.AuthorDto;
import com.bookworm.dto.ProductDto;
import com.bookworm.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorDto.AuthorResponse>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(authorService.listAll(page, size));
    }

    @PostMapping
    public ResponseEntity<AuthorDto.AuthorResponse> create(
            @Valid @RequestBody AuthorDto.AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(request));
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorDto.AuthorResponse> getById(@PathVariable Long authorId) {
        return ResponseEntity.ok(authorService.getById(authorId));
    }

    @PutMapping("/{authorId}")
    public ResponseEntity<AuthorDto.AuthorResponse> update(
            @PathVariable Long authorId,
            @Valid @RequestBody AuthorDto.AuthorRequest request) {
        return ResponseEntity.ok(authorService.update(authorId, request));
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<Void> delete(@PathVariable Long authorId) {
        authorService.delete(authorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{authorId}/products")
    public ResponseEntity<Page<ProductDto.ProductSummary>> getProducts(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(authorService.getProductsByAuthor(authorId, page, size));
    }
}
