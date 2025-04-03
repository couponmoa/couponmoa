package com.couponmoa.backend.domain.usercoupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCouponRequest {
    @NotBlank
    private final String code;
}
