package com.bookworm.controller;

import com.bookworm.dto.PaymentDto;
import com.bookworm.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentDto.PaymentSessionResponse> initiate(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentDto.InitiateRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(userDetails.getUsername(), request));
    }

    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentDto.PaymentResultResponse> confirm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String paymentId,
            @Valid @RequestBody PaymentDto.ConfirmRequest request) {
        return ResponseEntity.ok(
                paymentService.confirmPayment(userDetails.getUsername(), paymentId, request));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto.PaymentResultResponse> getStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getStatus(userDetails.getUsername(), paymentId));
    }
}
