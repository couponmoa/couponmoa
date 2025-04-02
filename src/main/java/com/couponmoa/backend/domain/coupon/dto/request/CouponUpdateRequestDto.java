package com.couponmoa.backend.domain.coupon.dto.request;

import com.couponmoa.backend.domain.coupon.enums.CouponCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponUpdateRequestDto {

    private String name;
    private int newTotalQuantity;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal discountRate = BigDecimal.ZERO;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount = BigDecimal.valueOf(9_999_999);
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private CouponCategory category;

    @Builder
    public CouponUpdateRequestDto(String name, int newTotalQuantity,
                                BigDecimal discountAmount, BigDecimal discountRate,
                                BigDecimal minOrderAmount, BigDecimal maxDiscountAmount,
                                String description, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate,
                                CouponCategory category) {
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
        this.category = category;
    }
}
