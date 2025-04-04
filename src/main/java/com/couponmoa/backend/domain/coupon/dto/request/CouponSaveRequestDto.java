package com.couponmoa.backend.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
}