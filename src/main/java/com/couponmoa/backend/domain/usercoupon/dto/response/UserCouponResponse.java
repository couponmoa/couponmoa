package com.couponmoa.backend.domain.usercoupon.dto.response;

import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.repository.projection.UserCouponProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserCouponResponse {
    private final Long id;
    private final UserCouponStatus status;
    private final BigDecimal discountAmount;
    private final BigDecimal discountRate;
    private final String name;
    private final String description;
    private final LocalDateTime expiryDate;
    private final BigDecimal minOrderAmount;
    private final BigDecimal maxDiscountAmount;

    public static UserCouponResponse from(UserCouponProjection projection) {
        return new UserCouponResponse(
                projection.getId(),
                projection.getStatus(),
                projection.getDiscountAmount(),
                projection.getDiscountRate(),
                projection.getName(),
                projection.getDescription(),
                projection.getExpiryDate(),
                projection.getMinOrderAmount(),
                projection.getMaxDiscountAmount()
        );
    }
}
