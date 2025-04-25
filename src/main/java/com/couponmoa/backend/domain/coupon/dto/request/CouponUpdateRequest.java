package com.couponmoa.backend.domain.coupon.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponUpdateRequest {

    private String name;
    private int newTotalQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private Long storeId;

    @Builder
    public CouponUpdateRequest(
            String name,
            int newTotalQuantity,
            BigDecimal discountAmount,
            BigDecimal discountRate,
            BigDecimal minOrderAmount,
            BigDecimal maxDiscountAmount,
            String description,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime expiryDate,
            Long storeId
    ) {
        this.name = name;
        this.newTotalQuantity = newTotalQuantity;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.storeId = storeId;
    }
}

