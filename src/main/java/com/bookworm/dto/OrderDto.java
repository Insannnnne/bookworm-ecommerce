package com.bookworm.dto;

import com.bookworm.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data
    public static class OrderRequest {
        @NotNull private Long addressId;
        @NotNull private Order.PaymentMethod paymentMethod;
        private String couponCode;
    }

    @Data
    public static class OrderItemResponse {
        private Long id;
        private ProductDto.ProductSummary product;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private LocalDate estimatedDeliveryDate;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private Order.Status status;
        private List<OrderItemResponse> items;
        private AddressDto.AddressResponse deliveryAddress;
        private BigDecimal subTotal;
        private BigDecimal tax;
        private BigDecimal deliveryCharges;
        private BigDecimal discount;
        private BigDecimal totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private LocalDateTime placedAt;
    }

    @Data
    public static class OrderConfirmationResponse {
        private Long orderId;
        private String message;
        private List<OrderItemResponse> items;
        private BigDecimal totalAmount;
        private String paymentStatus;
        private String transactionId;
        private LocalDateTime placedAt;
    }
}
