package com.bookworm.service;

import com.bookworm.dto.PaymentDto;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentDto.PaymentSessionResponse initiatePayment(String email,
                                                              PaymentDto.InitiateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        String paymentId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .payableAmount(order.getTotalAmount())
                .status(Order.PaymentStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        payment = paymentRepository.save(payment);

        PaymentDto.PaymentSessionResponse r = new PaymentDto.PaymentSessionResponse();
        r.setPaymentId(payment.getPaymentId());
        r.setOrderId(order.getId());
        r.setPayableAmount(payment.getPayableAmount());
        r.setPaymentMethod(payment.getPaymentMethod().name());
        r.setExpiresAt(payment.getExpiresAt());
        return r;
    }

    @Transactional
    public PaymentDto.PaymentResultResponse confirmPayment(String email, String paymentId,
                                                            PaymentDto.ConfirmRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment session not found"));
        if (!payment.getOrder().getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Payment not found");
        }
        if (payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            payment.setStatus(Order.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new IllegalStateException("Payment session has expired");
        }

        // Simulate successful payment
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setStatus(Order.PaymentStatus.SUCCESS);
        payment.setTransactionId(txnId);
        payment.setPaidAt(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setStatus(Order.Status.CONFIRMED);
        orderRepository.save(order);
        payment = paymentRepository.save(payment);

        PaymentDto.PaymentResultResponse r = new PaymentDto.PaymentResultResponse();
        r.setPaymentId(payment.getPaymentId());
        r.setOrderId(order.getId());
        r.setStatus(payment.getStatus().name());
        r.setTransactionId(payment.getTransactionId());
        r.setPaidAmount(payment.getPayableAmount());
        r.setPaidAt(payment.getPaidAt());
        return r;
    }

    @Transactional(readOnly = true)
    public PaymentDto.PaymentResultResponse getStatus(String email, String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!payment.getOrder().getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Payment not found");
        }
        PaymentDto.PaymentResultResponse r = new PaymentDto.PaymentResultResponse();
        r.setPaymentId(payment.getPaymentId());
        r.setOrderId(payment.getOrder().getId());
        r.setStatus(payment.getStatus().name());
        r.setTransactionId(payment.getTransactionId());
        r.setPaidAmount(payment.getPayableAmount());
        r.setPaidAt(payment.getPaidAt());
        return r;
    }
}
