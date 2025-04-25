package com.couponmoa.backend.domain.couponstats.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.couponstats.dto.request.CouponUsageSearchRequest;
import com.couponmoa.backend.domain.couponstats.dto.response.CouponUsageResponse;
import com.couponmoa.backend.domain.couponstats.service.CouponStatsService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "쿠폰 통계 API", description = "발급한 쿠폰에 대한 통계를 조회할 수 있는 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CouponStatsController {

    private final CouponStatsService couponStatsService;

    @Operation(summary = "쿠폰 일별 사용량 조회")
    @Secured(UserRole.Authority.ADMIN)
    @GetMapping("/coupons/{couponId}/usage-stats")
    public ApiResponse<List<CouponUsageResponse>> findCouponDailyUsageStats(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long couponId,
            @Valid @ModelAttribute CouponUsageSearchRequest request
    ) {
        List<CouponUsageResponse> response = couponStatsService.findCouponDailyUsageStats(authUser.getId(), couponId, request);
        return ApiResponse.success(response);
    }
}
