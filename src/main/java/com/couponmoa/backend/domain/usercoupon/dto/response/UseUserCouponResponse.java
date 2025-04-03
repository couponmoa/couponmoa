package com.couponmoa.backend.domain.usercoupon.dto.response;

import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class UseUserCouponResponse {
    private Long id;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private String name;
    private String description;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;

    public static UseUserCouponResponse from(UserCoupon userCoupon) {
        return new UseUserCouponResponse(
                userCoupon.getId(),
                userCoupon.getCoupon().getDiscountAmount(),
                userCoupon.getCoupon().getDiscountRate(),
                userCoupon.getCoupon().getName(),
                userCoupon.getCoupon().getDescription(),
                userCoupon.getCoupon().getMinOrderAmount(),
                userCoupon.getCoupon().getMaxDiscountAmount()
        );
    }
}
