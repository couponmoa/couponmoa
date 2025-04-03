package com.couponmoa.backend.domain.coupon.dto.response;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CouponDetailResponseDto {

    private Long id;
    private String name;
    private int totalQuantity;
    private int availableQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CouponDetailResponseDto toDto(Coupon coupon) {
        return CouponDetailResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .totalQuantity(coupon.getTotalQuantity())
                .availableQuantity(coupon.getAvailableQuantity())
                .discountAmount(coupon.getDiscountAmount())
                .discountRate(coupon.getDiscountRate())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .description(coupon.getDescription())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .expiryDate(coupon.getExpiryDate())
                .createdAt(coupon.getCreatedAt())
                .modifiedAt(coupon.getModifiedAt())
                .build();
    }
}
