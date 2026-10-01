package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EntityMapper {

    private final ReviewRepository reviewRepository;

    public AuthorDto.AuthorResponse toAuthorResponse(Author author) {
        if (author == null) return null;
        AuthorDto.AuthorResponse r = new AuthorDto.AuthorResponse();
        r.setId(author.getId());
        r.setName(author.getName());
        r.setBio(author.getBio());
        r.setProfileImageUrl(author.getProfileImageUrl());
        return r;
    }

    public CategoryDto.CategoryResponse toCategoryResponse(Category category) {
        CategoryDto.CategoryResponse r = new CategoryDto.CategoryResponse();
        r.setId(category.getId());
        r.setName(category.getName());
        r.setDescription(category.getDescription());
        r.setProductCount(0L);
        return r;
    }

    public ProductDto.ProductSummary toProductSummary(Product p) {
        ProductDto.ProductSummary s = new ProductDto.ProductSummary();
        s.setId(p.getId());
        s.setTitle(p.getTitle());
        s.setAuthorName(p.getAuthor() != null ? p.getAuthor().getName() : null);
        s.setCoverImageUrl(p.getCoverImageUrl());
        s.setPrice(p.getPrice());
        s.setFormat(p.getFormat() != null ? p.getFormat().name() : null);
        s.setCategories(p.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        Double avg = reviewRepository.findAverageRatingByProductId(p.getId());
        s.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        s.setDeliveryDate(LocalDate.now().plusDays(3));
        return s;
    }

    public ProductDto.ProductResponse toProductResponse(Product p) {
        ProductDto.ProductResponse r = new ProductDto.ProductResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setAuthorName(p.getAuthor() != null ? p.getAuthor().getName() : null);
        r.setCoverImageUrl(p.getCoverImageUrl());
        r.setPrice(p.getPrice());
        r.setFormat(p.getFormat() != null ? p.getFormat().name() : null);
        r.setCategories(p.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        Double avg = reviewRepository.findAverageRatingByProductId(p.getId());
        r.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        r.setDeliveryDate(LocalDate.now().plusDays(3));
        r.setLanguage(p.getLanguage());
        r.setIsbn(p.getIsbn());
        r.setPublishedDate(p.getPublishedDate());
        r.setStock(p.getStock());
        r.setSoldCount(p.getSoldCount());
        return r;
    }

    public ProductDto.ProductDetailResponse toProductDetailResponse(Product p) {
        ProductDto.ProductDetailResponse r = new ProductDto.ProductDetailResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setAuthorName(p.getAuthor() != null ? p.getAuthor().getName() : null);
        r.setCoverImageUrl(p.getCoverImageUrl());
        r.setPrice(p.getPrice());
        r.setFormat(p.getFormat() != null ? p.getFormat().name() : null);
        r.setCategories(p.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        Double avg = reviewRepository.findAverageRatingByProductId(p.getId());
        r.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        r.setDeliveryDate(LocalDate.now().plusDays(3));
        r.setLanguage(p.getLanguage());
        r.setIsbn(p.getIsbn());
        r.setPublishedDate(p.getPublishedDate());
        r.setStock(p.getStock());
        r.setSoldCount(p.getSoldCount());
        r.setDescription(p.getDescription());
        r.setPublisher(p.getPublisher());
        r.setAuthor(toAuthorResponse(p.getAuthor()));
        r.setReviewCount((int) reviewRepository.countByProductId(p.getId()));
        return r;
    }

    public ReviewDto.ReviewResponse toReviewResponse(Review review) {
        ReviewDto.ReviewResponse r = new ReviewDto.ReviewResponse();
        r.setId(review.getId());
        r.setUserId(review.getUser().getId());
        r.setReviewerName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        r.setRating(review.getRating());
        r.setComment(review.getComment());
        r.setCreatedAt(review.getCreatedAt());
        return r;
    }

    public AddressDto.AddressResponse toAddressResponse(Address a) {
        AddressDto.AddressResponse r = new AddressDto.AddressResponse();
        r.setId(a.getId());
        r.setFirstName(a.getFirstName());
        r.setLastName(a.getLastName());
        r.setAddressLine1(a.getAddressLine1());
        r.setAddressLine2(a.getAddressLine2());
        r.setCity(a.getCity());
        r.setPinCode(a.getPinCode());
        r.setState(a.getState());
        r.setCountry(a.getCountry());
        r.setPhone(a.getPhone());
        return r;
    }

    public UserDto.UserResponse toUserResponse(User user) {
        UserDto.UserResponse r = new UserDto.UserResponse();
        r.setId(user.getId());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setEmail(user.getEmail());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }

    public OrderDto.OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderDto.OrderItemResponse r = new OrderDto.OrderItemResponse();
        r.setId(item.getId());
        r.setProduct(toProductSummary(item.getProduct()));
        r.setQuantity(item.getQuantity());
        r.setUnitPrice(item.getUnitPrice());
        r.setLineTotal(item.getLineTotal());
        r.setEstimatedDeliveryDate(item.getEstimatedDeliveryDate());
        return r;
    }

    public OrderDto.OrderResponse toOrderResponse(Order order) {
        OrderDto.OrderResponse r = new OrderDto.OrderResponse();
        r.setId(order.getId());
        r.setStatus(order.getStatus());
        r.setItems(order.getItems().stream().map(this::toOrderItemResponse).collect(Collectors.toList()));
        r.setDeliveryAddress(toAddressResponse(order.getDeliveryAddress()));
        r.setSubTotal(order.getSubTotal());
        r.setTax(order.getTax());
        r.setDeliveryCharges(order.getDeliveryCharges());
        r.setDiscount(order.getDiscount());
        r.setTotalAmount(order.getTotalAmount());
        r.setPaymentMethod(order.getPaymentMethod().name());
        r.setPaymentStatus(order.getPaymentStatus().name());
        r.setPlacedAt(order.getPlacedAt());
        return r;
    }
}
