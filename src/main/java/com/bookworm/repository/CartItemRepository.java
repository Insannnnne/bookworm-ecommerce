package com.bookworm.repository;

import com.bookworm.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndCartUserId(Long id, Long userId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
