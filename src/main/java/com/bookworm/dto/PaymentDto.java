package com.bookworm.dto;

import com.bookworm.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    @Data
    public static class InitiateRequest {
        @NotNull private Long orderId;
        @NotNull private Order.PaymentMethod paymentMethod;
    }

    @Data
    public static class ConfirmRequest {
        private Order.PaymentMethod paymentMethod;
        private String cardNumber;
        private String nameOnCard;
        private String expiryDate;
        private String cvv;
        private String upiId;
    }

    @Data
    public static class PaymentSessionResponse {
        private String paymentId;
        private Long orderId;
        private BigDecimal payableAmount;
        private String paymentMethod;
        private LocalDateTime expiresAt;
    }

    @Data
    public static class PaymentResultResponse {
        private String paymentId;
        private Long orderId;
        private String status;
        private String transactionId;
        private BigDecimal paidAmount;
        private LocalDateTime paidAt;
    }
}
