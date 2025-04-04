package com.couponmoa.backend.domain.usercoupon.repository.projection;

import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UserCouponProjection {
    Long getId();
    UserCouponStatus getStatus();
    BigDecimal getDiscountAmount();
    BigDecimal getDiscountRate();
    String getName();
    String getDescription();
    LocalDateTime getExpiryDate();
    BigDecimal getMinOrderAmount();
    BigDecimal getMaxDiscountAmount();
}
