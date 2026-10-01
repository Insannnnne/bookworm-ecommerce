package com.bookworm.service;

import com.bookworm.dto.CategoryDto;
import com.bookworm.entity.Category;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityMapper mapper;

    public List<CategoryDto.CategoryResponse> listAll() {
        return categoryRepository.findAll()
                .stream().map(mapper::toCategoryResponse).collect(Collectors.toList());
    }

    public CategoryDto.CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return mapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryDto.CategoryResponse create(CategoryDto.CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto.CategoryResponse update(Long id, CategoryDto.CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        categoryRepository.delete(category);
    }
}
