package com.couponmoa.backend.domain.usercoupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponCodeResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.service.UserCouponService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: userId를 PathVariable에서 Jwt로 변경
 * TODO: API 별 권한 체크 추가
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserCouponController {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    private final UserCouponService userCouponService;

    @PostMapping("/v1/coupons/{couponId}/issue/{userId}")
    public ApiResponse<Void> createUserCoupon(
            @PathVariable Long userId,
            @PathVariable Long couponId
    ) {
        userCouponService.createUserCoupon(userId, couponId);
        return ApiResponse.success();
    }

    @GetMapping("/v1/user-coupons/{userId}")
    public ApiResponse<Page<UserCouponResponse>> findUserCoupons(
            @PathVariable Long userId,
            @RequestParam(required = false) UserCouponStatus status,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) @Min(1) Integer page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) @Min(1) Integer size
    ) {
        Page<UserCouponResponse> response = userCouponService.findUserCoupons(userId, status, page, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/v1/user-coupons/{userCouponId}/code/{userId}")
    public ApiResponse<UserCouponCodeResponse> findUserCouponCode(
            @PathVariable Long userId,
            @PathVariable Long userCouponId
    ) {
        UserCouponCodeResponse response = userCouponService.findUserCouponCode(userId, userCouponId);
        return ApiResponse.success(response);
    }
}
