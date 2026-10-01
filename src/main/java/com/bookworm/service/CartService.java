package com.bookworm.service;

import com.bookworm.dto.CartDto;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500");
    private static final BigDecimal DELIVERY_CHARGE = new BigDecimal("50");

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    @Transactional(readOnly = true)
    public CartDto.CartResponse getCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = getOrCreateCart(user);
        return toCartResponse(cart);
    }

    @Transactional
    public CartDto.CartResponse addItem(String email, CartDto.CartItemRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Cart cart = getOrCreateCart(user);
        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                    existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                    () -> {
                        CartItem item = CartItem.builder()
                                .cart(cart).product(product)
                                .quantity(request.getQuantity()).build();
                        cart.getItems().add(item);
                    }
                );
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse updateItem(String email, Long cartItemId,
                                           CartDto.CartItemUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CartItem item = cartItemRepository.findByIdAndCartUserId(cartItemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return toCartResponse(cart);
    }

    @Transactional
    public CartDto.CartResponse removeItem(String email, Long cartItemId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CartItem item = cartItemRepository.findByIdAndCartUserId(cartItemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse applyCoupon(String email, CartDto.CouponRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = getOrCreateCart(user);
        // Simplified coupon logic: fixed ₹100 discount for demo
        if (!"BOOKWORM10".equalsIgnoreCase(request.getCode())) {
            throw new IllegalArgumentException("Invalid or expired coupon code");
        }
        cart.setCouponCode(request.getCode().toUpperCase());
        cart.setDiscount(new BigDecimal("100"));
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse removeCoupon(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = getOrCreateCart(user);
        cart.setCouponCode(null);
        cart.setDiscount(BigDecimal.ZERO);
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cart.setCouponCode(null);
        cart.setDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    public CartDto.CartResponse toCartResponse(Cart cart) {
        CartDto.CartResponse r = new CartDto.CartResponse();
        r.setId(cart.getId());
        r.setItems(cart.getItems().stream().map(item -> {
            CartDto.CartItemResponse ci = new CartDto.CartItemResponse();
            ci.setId(item.getId());
            ci.setProduct(mapper.toProductSummary(item.getProduct()));
            ci.setQuantity(item.getQuantity());
            ci.setLineTotal(item.getLineTotal());
            return ci;
        }).collect(Collectors.toList()));

        BigDecimal subTotal = cart.getItems().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal delivery = subTotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO : DELIVERY_CHARGE;
        BigDecimal discount = cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO;
        BigDecimal total = subTotal.add(tax).add(delivery).subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);

        r.setSubTotal(subTotal);
        r.setTax(tax);
        r.setDeliveryCharges(delivery);
        r.setCouponCode(cart.getCouponCode());
        r.setDiscount(discount);
        r.setTotalAmount(total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total);
        return r;
    }
}
