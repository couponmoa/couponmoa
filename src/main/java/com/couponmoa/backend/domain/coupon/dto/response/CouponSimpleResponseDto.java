package com.couponmoa.backend.domain.coupon.dto.response;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CouponSimpleResponseDto implements Serializable {// implements Serializable 캐싱을 위해 Redis에 객체를 저장할 때, 직렬화 필요
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

    @Builder
    public CouponSimpleResponseDto(Long id, String name, BigDecimal discountAmount, BigDecimal discountRate, int availableQuantity) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.availableQuantity = availableQuantity;
    }

    @Builder
    public CouponSimpleResponseDto(Long id, String name, int availableQuantity, BigDecimal discountAmount, BigDecimal discountRate, LocalDateTime startDate, LocalDateTime endDate, CouponStatus status) {
        this.id = id;
        this.name = name;
        this.availableQuantity = availableQuantity;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static CouponSimpleResponseDto toDto(Coupon coupon) {
        return CouponSimpleResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .availableQuantity(coupon.getAvailableQuantity())
                .discountAmount(coupon.getDiscountAmount())
                .discountRate(coupon.getDiscountRate())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .status(CouponStatus.editStatus(coupon.getStartDate(), coupon.getEndDate(), coupon.getAvailableQuantity()))
                .build();
    }
}
