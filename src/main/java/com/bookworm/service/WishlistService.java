package com.bookworm.service;

import com.bookworm.dto.WishlistDto;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<WishlistDto.WishlistItemResponse> getWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return wishlistRepository.findByUserId(user.getId())
                .stream().map(item -> {
                    WishlistDto.WishlistItemResponse r = new WishlistDto.WishlistItemResponse();
                    r.setId(item.getId());
                    r.setProduct(mapper.toProductSummary(item.getProduct()));
                    r.setAddedAt(item.getAddedAt());
                    return r;
                }).collect(Collectors.toList());
    }

    @Transactional
    public WishlistDto.WishlistItemResponse addToWishlist(String email,
                                                          WishlistDto.WishlistRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new IllegalArgumentException("Product already in wishlist");
        }
        WishlistItem item = WishlistItem.builder()
                .user(user).product(product).build();
        item = wishlistRepository.save(item);
        WishlistDto.WishlistItemResponse r = new WishlistDto.WishlistItemResponse();
        r.setId(item.getId());
        r.setProduct(mapper.toProductSummary(product));
        r.setAddedAt(item.getAddedAt());
        return r;
    }

    @Transactional
    public void removeFromWishlist(String email, Long productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        WishlistItem item = wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlistRepository.delete(item);
    }
}
