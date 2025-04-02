package com.couponmoa.backend.domain.subscribe.usercoupon.dto.response;


import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponCategory;
import com.couponmoa.backend.domain.subscribe.usercoupon.entity.UserCouponSubscribe;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FindCouponSubscribeListResponse {
    private Long id;
    private String name;
    private int availableQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private CouponCategory counponCategory;

    public FindCouponSubscribeListResponse(UserCouponSubscribe userCouponSubscribe) {
        Coupon coupon = userCouponSubscribe.getCoupon();
        this.availableQuantity = coupon.getAvailableQuantity();
        this.counponCategory = coupon.getCounponCategory();
        this.description = coupon.getDescription();
        this.discountAmount = coupon.getDiscountAmount();
        this.discountRate = coupon.getDiscountRate();
        this.endDate = coupon.getEndDate();
        this.expiryDate = coupon.getExpiryDate();
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.startDate = coupon.getStartDate();
    }
}
