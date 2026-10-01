package com.bookworm.repository;

import com.bookworm.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    Page<Product> findByCategoriesId(Long categoryId, Pageable pageable);

    Page<Product> findByAuthorId(Long authorId, Pageable pageable);

    List<Product> findByFeaturedTrue(Pageable pageable);

    List<Product> findByBestsellerTrue(Pageable pageable);

    List<Product> findByNewLaunchTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id <> :productId " +
           "AND EXISTS (SELECT c FROM p.categories c WHERE c IN " +
           "(SELECT c2 FROM Product p2 JOIN p2.categories c2 WHERE p2.id = :productId)) " +
           "ORDER BY p.soldCount DESC")
    List<Product> findRelatedProducts(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id IN " +
           "(SELECT oi.product.id FROM OrderItem oi WHERE oi.order.user.id = :userId) " +
           "AND p.id NOT IN " +
           "(SELECT oi2.product.id FROM OrderItem oi2 WHERE oi2.order.user.id = :userId AND oi2.order.placedAt > " +
           "(SELECT MAX(o3.placedAt) FROM Order o3 WHERE o3.user.id = :userId)) " +
           "ORDER BY p.soldCount DESC")
    List<Product> findRecommendationsForUser(@Param("userId") Long userId, Pageable pageable);
}
