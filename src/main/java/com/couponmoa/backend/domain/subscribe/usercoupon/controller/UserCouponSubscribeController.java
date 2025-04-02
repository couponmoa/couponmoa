package com.couponmoa.backend.domain.subscribe.usercoupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.subscribe.usercoupon.service.UserCouponSubscribeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 구독 API", description = "유저 쿠폰 구독 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/coupons")
public class UserCouponSubscribeController {

    private final UserCouponSubscribeService userCouponSubServ;

    @Operation(summary = "쿠폰 구독", description = "유저가 특정 쿠폰을 구독함")
    @PostMapping("/{couponId}/subscriptions")
    public ResponseEntity<ApiResponse<Void>> subscribeCoupon(
            @Parameter(description = "사용자 ID", required = true)
            @RequestBody Long userId,

            @Parameter(description = "구독할 쿠폰 id", required = true)
            @PathVariable Long couponId) {

        Long userCouponSubId = userCouponSubServ.subscribeCoupon(userId, couponId);

        return ResponseEntity.ok(ApiResponse.success(userCouponSubId + "번 쿠폰 구독 완료"));
    }
}
