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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 쿠폰 API", description = "쿠폰을 발급받고, 발급받은 쿠폰을 관리할 수 있는 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserCouponController {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    private final UserCouponService userCouponService;

    @Operation(summary = "쿠폰 발급 (동기)")
    @Secured(UserRole.Authority.USER)
    @PostMapping("/v1/coupons/{couponId}/issue")
    public ApiResponse<Void> createUserCouponSync(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long couponId
    ) {
        userCouponService.createUserCouponSync(authUser.getId(), couponId);
        return ApiResponse.success();
    }

    @Operation(summary = "쿠폰 발급 (비동기)")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured(UserRole.Authority.USER)
    @PostMapping("/v2/coupons/{couponId}/issue")
    public ApiResponse<Void> createUserCouponAsync(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long couponId
    ) {
        userCouponService.createUserCouponAsync(authUser.getId(), couponId);
        return ApiResponse.of(HttpStatus.ACCEPTED, "쿠폰 발급 요청이 접수되었습니다.", null);
    }

    @Operation(summary = "발급받은 쿠폰 목록 조회")
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

    @Operation(summary = "쿠폰 코드 조회")
    @Secured(UserRole.Authority.USER)
    @GetMapping("/v1/user-coupons/{userCouponId}/code")
    public ApiResponse<UserCouponCodeResponse> findUserCouponCode(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long userCouponId
    ) {
        UserCouponCodeResponse response = userCouponService.findUserCouponCode(authUser.getId(), userCouponId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "쿠폰 사용 처리")
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
