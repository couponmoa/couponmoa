package com.couponmoa.backend.domain.usercoupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.usercoupon.dto.request.UserCouponRequest;
import com.couponmoa.backend.domain.usercoupon.dto.response.UseUserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponCodeResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.service.UserCouponService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserCouponController {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    private final UserCouponService userCouponService;

    @Secured(UserRole.Authority.USER)
    @PostMapping("/v1/coupons/{couponId}/issue")
    public ApiResponse<Void> createUserCoupon(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long couponId
    ) {
        userCouponService.createUserCoupon(authUser.getId(), couponId);
        return ApiResponse.success();
    }

    @Secured(UserRole.Authority.USER)
    @GetMapping("/v1/user-coupons")
    public ApiResponse<Page<UserCouponResponse>> findUserCoupons(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) UserCouponStatus status,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) @Min(1) Integer page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) @Min(1) Integer size
    ) {
        Page<UserCouponResponse> response = userCouponService.findUserCoupons(authUser.getId(), status, page, size);
        return ApiResponse.success(response);
    }

    @Secured(UserRole.Authority.USER)
    @GetMapping("/v1/user-coupons/{userCouponId}/code")
    public ApiResponse<UserCouponCodeResponse> findUserCouponCode(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long userCouponId
    ) {
        UserCouponCodeResponse response = userCouponService.findUserCouponCode(authUser.getId(), userCouponId);
        return ApiResponse.success(response);
    }

    @Secured(UserRole.Authority.ADMIN)
    @PostMapping("/v1/user-coupons/use")
    public ApiResponse<UseUserCouponResponse> useUserCoupon(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserCouponRequest request
    ) {
        UseUserCouponResponse response = userCouponService.useUserCoupon(authUser.getId(), request);
        return ApiResponse.success(response);
    }
}
