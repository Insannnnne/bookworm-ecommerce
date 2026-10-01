package com.bookworm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    private String paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Order.PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal payableAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Order.PaymentStatus status = Order.PaymentStatus.PENDING;

    private String transactionId;

    private LocalDateTime paidAt;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
}
