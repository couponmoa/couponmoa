package com.couponmoa.backend.domain.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponResponseDto {

    private final Long id;

    public CouponResponseDto(Long id) {
        this.id = id;
    }
}
