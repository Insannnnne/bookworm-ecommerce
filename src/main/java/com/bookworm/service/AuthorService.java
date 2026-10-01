package com.bookworm.service;

import com.bookworm.dto.AuthorDto;
import com.bookworm.dto.ProductDto;
import com.bookworm.entity.Author;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.AuthorRepository;
import com.bookworm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    public Page<AuthorDto.AuthorResponse> listAll(int page, int size) {
        return authorRepository.findAll(PageRequest.of(page, size))
                .map(mapper::toAuthorResponse);
    }

    public AuthorDto.AuthorResponse getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
        return mapper.toAuthorResponse(author);
    }

    @Transactional
    public AuthorDto.AuthorResponse create(AuthorDto.AuthorRequest request) {
        Author author = Author.builder()
                .name(request.getName())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .build();
        return mapper.toAuthorResponse(authorRepository.save(author));
    }

    @Transactional
    public AuthorDto.AuthorResponse update(Long id, AuthorDto.AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
        author.setName(request.getName());
        author.setBio(request.getBio());
        author.setProfileImageUrl(request.getProfileImageUrl());
        return mapper.toAuthorResponse(authorRepository.save(author));
    }

    @Transactional
    public void delete(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
        authorRepository.delete(author);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto.ProductSummary> getProductsByAuthor(Long authorId, int page, int size) {
        authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + authorId));
        return productRepository.findByAuthorId(authorId, PageRequest.of(page, size))
                .map(mapper::toProductSummary);
    }
}
