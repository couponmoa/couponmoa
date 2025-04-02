package com.couponmoa.backend.domain.coupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSaveRequestDto;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponseDto;
import com.couponmoa.backend.domain.coupon.service.CouponService;
import com.couponmoa.backend.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

// 차후에 responsebody 메세지 추가예정.
@Tag(name = "쿠폰 API", description = "쿠폰 관련 기능을 제공하는 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "쿠폰 생성", description = "관리자가 새로운 쿠폰을 생성함.")
    @Secured(UserRole.Authority.ADMIN)
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponseDto>> createCoupon(
            @Valid @RequestBody CouponSaveRequestDto requestDto) {
        return ResponseEntity.ok(couponService.createCoupon(requestDto));
    }

    @Operation(summary = "쿠폰 수정", description = "관리자가 특정 쿠폰 정보를 수정함.")
    @Secured(UserRole.Authority.ADMIN)
    @PutMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponUpdateRequestDto requestDto) {
        return ResponseEntity.ok(couponService.updateCoupon(couponId, requestDto));
    }

    @Operation(summary = "쿠폰 삭제", description = "관리자가 특정 쿠폰을 삭제함.")
    @Secured(UserRole.Authority.ADMIN)
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}
