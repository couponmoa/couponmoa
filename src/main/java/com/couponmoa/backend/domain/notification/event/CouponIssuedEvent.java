package com.couponmoa.backend.domain.notification.event;

import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponIssuedEvent {
    private final Long userId;
    private final UserCoupon userCoupon;
    private final Long notificationId;
}
