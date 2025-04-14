package com.couponmoa.backend.domain.coupon.dto.response;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CouponSimpleResponseDto {
    private Long id;
    private String name;
    private int availableQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;
    private CouponStatus status;

    public static CouponSimpleResponseDto toDto(Coupon coupon) {
        return CouponSimpleResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .availableQuantity(coupon.getAvailableQuantity())
                .discountAmount(coupon.getDiscountAmount())
                .discountRate(coupon.getDiscountRate())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .status(coupon.getStatus())
                .build();
    }
}
