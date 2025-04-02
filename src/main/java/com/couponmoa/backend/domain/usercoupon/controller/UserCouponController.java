package com.couponmoa.backend.domain.usercoupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.usercoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: userId를 PathVariable에서 Jwt로 변경
 * TODO: API 별 권한 체크 추가
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping("/v1/coupons/{couponId}/issue/{userId}")
    public ApiResponse<Void> createUserCoupon(
            @PathVariable Long userId,
            @PathVariable Long couponId
    ) {
        userCouponService.createUserCoupon(userId, couponId);
        return ApiResponse.success();
    }
}
