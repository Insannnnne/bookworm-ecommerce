package com.bookworm.service;

import com.bookworm.dto.ProductDto;
import com.bookworm.dto.ReviewDto;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductDto.ProductSummary> listAll(int page, int size, String sort) {
        return productRepository.findAll(PageRequest.of(page, size))
                .map(mapper::toProductSummary);
    }

    @Transactional(readOnly = true)
    public ProductDto.ProductDetailResponse getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return mapper.toProductDetailResponse(p);
    }

    @Transactional
    public ProductDto.ProductResponse create(ProductDto.ProductRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        Set<Category> categories = new HashSet<>(
                categoryRepository.findAllById(request.getCategoryIds())
        );
        Product product = Product.builder()
                .title(request.getTitle())
                .author(author)
                .publisher(request.getPublisher())
                .description(request.getDescription())
                .price(request.getPrice())
                .format(request.getFormat())
                .language(request.getLanguage())
                .coverImageUrl(request.getCoverImageUrl())
                .isbn(request.getIsbn())
                .publishedDate(request.getPublishedDate())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .featured(Boolean.TRUE.equals(request.getFeatured()))
                .newLaunch(Boolean.TRUE.equals(request.getNewLaunch()))
                .bestseller(Boolean.TRUE.equals(request.getBestseller()))
                .categories(categories)
                .build();
        return mapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductDto.ProductResponse update(Long id, ProductDto.ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        Set<Category> categories = new HashSet<>(
                categoryRepository.findAllById(request.getCategoryIds())
        );
        product.setTitle(request.getTitle());
        product.setAuthor(author);
        product.setPublisher(request.getPublisher());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setFormat(request.getFormat());
        product.setLanguage(request.getLanguage());
        product.setCoverImageUrl(request.getCoverImageUrl());
        product.setIsbn(request.getIsbn());
        product.setPublishedDate(request.getPublishedDate());
        product.setStock(request.getStock() != null ? request.getStock() : product.getStock());
        product.setFeatured(Boolean.TRUE.equals(request.getFeatured()));
        product.setNewLaunch(Boolean.TRUE.equals(request.getNewLaunch()));
        product.setBestseller(Boolean.TRUE.equals(request.getBestseller()));
        product.setCategories(categories);
        return mapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto.ProductSummary> search(String q, Long categoryId, Long authorId,
                                                   String language, String format,
                                                   BigDecimal minPrice, BigDecimal maxPrice,
                                                   String sort, int page, int size) {
        Specification<Product> spec = Specification.where(null);
        if (q != null && !q.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("author").get("name")), "%" + q.toLowerCase() + "%")
                    ));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join("categories").get("id"), categoryId);
            });
        }
        if (authorId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("author").get("id"), authorId));
        }
        if (language != null && !language.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
        }
        if (format != null && !format.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("format").as(String.class), format));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        return productRepository.findAll(spec, PageRequest.of(page, size))
                .map(mapper::toProductSummary);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto.ProductSummary> getByCategory(Long categoryId, int page, int size) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        return productRepository.findByCategoriesId(categoryId, PageRequest.of(page, size))
                .map(mapper::toProductSummary);
    }

    @Transactional(readOnly = true)
    public List<ProductDto.ProductSummary> getRelated(Long productId, int limit) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return productRepository.findRelatedProducts(productId, PageRequest.of(0, limit))
                .stream().map(mapper::toProductSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto.ProductSummary> getFeatured(String section, int limit) {
        List<Product> products;
        PageRequest pageRequest = PageRequest.of(0, limit);
        products = switch (section) {
            case "recommended" -> productRepository.findByFeaturedTrue(pageRequest);
            case "bestsellers" -> productRepository.findByBestsellerTrue(pageRequest);
            case "new_launches" -> productRepository.findByNewLaunchTrue(pageRequest);
            default -> List.of();
        };
        return products.stream().map(mapper::toProductSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto.ProductSummary> getRecommendationsForUser(Long userId, int limit) {
        return productRepository.findRecommendationsForUser(userId, PageRequest.of(0, limit))
                .stream().map(mapper::toProductSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto.ReviewResponse> getReviews(Long productId, int page, int size) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return reviewRepository.findByProductId(productId, PageRequest.of(page, size))
                .map(mapper::toReviewResponse);
    }

    @Transactional
    public ReviewDto.ReviewResponse addReview(Long productId, String userEmail,
                                               ReviewDto.ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return mapper.toReviewResponse(reviewRepository.save(review));
    }
}
