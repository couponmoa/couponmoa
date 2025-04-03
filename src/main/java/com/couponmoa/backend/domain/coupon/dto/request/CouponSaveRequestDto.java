package com.couponmoa.backend.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponSaveRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private int totalQuantity;
    @NotNull
    private BigDecimal discountAmount = BigDecimal.ZERO;
    @NotNull
    private BigDecimal discountRate = BigDecimal.ZERO;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private Long storeId;

    @Builder
    public CouponSaveRequestDto(String name, int totalQuantity,
                                BigDecimal discountAmount, BigDecimal discountRate,
                                BigDecimal minOrderAmount, BigDecimal maxDiscountAmount,
                                String description, LocalDateTime startDate, LocalDateTime endDate,
                                LocalDateTime expiryDate, Long storeId) {
        this.name = name;
        this.totalQuantity = totalQuantity;
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