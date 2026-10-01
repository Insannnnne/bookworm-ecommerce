package com.bookworm.controller;

import com.bookworm.dto.CartDto;
import com.bookworm.dto.OrderDto;
import com.bookworm.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto.OrderResponse>> listOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orderService.listOrders(userDetails.getUsername(), page, size));
    }

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderDto.OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userDetails.getUsername(), request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.OrderResponse> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(userDetails.getUsername(), orderId));
    }

    @PostMapping("/{orderId}/buy-again")
    public ResponseEntity<CartDto.CartResponse> buyAgain(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.buyAgain(userDetails.getUsername(), orderId));
    }

    @GetMapping("/{orderId}/confirmation")
    public ResponseEntity<OrderDto.OrderConfirmationResponse> getConfirmation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getConfirmation(userDetails.getUsername(), orderId));
    }
}
