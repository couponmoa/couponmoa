package com.couponmoa.backend.domain.coupon.dto.response;

import lombok.Getter;

@Getter
public class CouponResponse {

    private final Long id;

    public CouponResponse(Long id) {
        this.id = id;
    }
}
