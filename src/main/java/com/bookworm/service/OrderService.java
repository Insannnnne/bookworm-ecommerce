package com.bookworm.service;

import com.bookworm.dto.CartDto;
import com.bookworm.dto.OrderDto;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500");
    private static final BigDecimal DELIVERY_CHARGE = new BigDecimal("50");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final EntityMapper mapper;
    private final CartService cartService;

    @Transactional(readOnly = true)
    public Page<OrderDto.OrderResponse> listOrders(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserIdOrderByPlacedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(mapper::toOrderResponse);
    }

    @Transactional
    public OrderDto.OrderResponse createOrder(String email, OrderDto.OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal subTotal = cart.getItems().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal delivery = subTotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO : DELIVERY_CHARGE;
        BigDecimal discount = cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO;
        BigDecimal total = subTotal.add(tax).add(delivery).subtract(discount)
                .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
                .user(user)
                .deliveryAddress(address)
                .paymentMethod(request.getPaymentMethod())
                .subTotal(subTotal)
                .tax(tax)
                .deliveryCharges(delivery)
                .discount(discount)
                .totalAmount(total)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getProduct().getPrice())
                    .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                    .build();
            orderItems.add(oi);
        }
        order.setItems(orderItems);
        order = orderRepository.save(order);

        // Clear the cart
        cart.getItems().clear();
        cart.setCouponCode(null);
        cart.setDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);

        return mapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderDto.OrderResponse getOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }
        return mapper.toOrderResponse(order);
    }

    @Transactional
    public CartDto.CartResponse buyAgain(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        for (OrderItem oi : order.getItems()) {
            cartItemRepository.findByCartIdAndProductId(cart.getId(), oi.getProduct().getId())
                    .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + oi.getQuantity()),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .cart(cart).product(oi.getProduct())
                                    .quantity(oi.getQuantity()).build();
                            cart.getItems().add(newItem);
                        }
                    );
        }
        return cartService.toCartResponse(cartRepository.save(cart));
    }

    @Transactional(readOnly = true)
    public OrderDto.OrderConfirmationResponse getConfirmation(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (order.getPaymentStatus() != Order.PaymentStatus.SUCCESS) {
            throw new ResourceNotFoundException("Order not yet confirmed");
        }
        String txnId = paymentRepository.findByOrderId(orderId)
                .map(p -> p.getTransactionId()).orElse(null);

        OrderDto.OrderConfirmationResponse r = new OrderDto.OrderConfirmationResponse();
        r.setOrderId(order.getId());
        r.setMessage("Your purchase of the following reads is successful");
        r.setItems(order.getItems().stream().map(mapper::toOrderItemResponse).collect(Collectors.toList()));
        r.setTotalAmount(order.getTotalAmount());
        r.setPaymentStatus(order.getPaymentStatus().name());
        r.setTransactionId(txnId);
        r.setPlacedAt(order.getPlacedAt());
        return r;
    }
}
