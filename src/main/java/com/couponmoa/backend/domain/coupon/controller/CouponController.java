package com.couponmoa.backend.domain.coupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSaveRequestDto;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.service.CouponReadService;
import com.couponmoa.backend.domain.coupon.service.CouponService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 차후에 responsebody 메세지 추가예정.
@Tag(name = "쿠폰 API", description = "쿠폰 관련 기능을 제공하는 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CouponReadService couponReadService;

    @Operation(summary = "쿠폰 생성", description = "관리자가 새로운 쿠폰을 생성함.")
    @Secured(UserRole.Authority.ADMIN)
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponseDto>> createCoupon(
            @Valid @RequestBody CouponSaveRequestDto requestDto) {
        return ResponseEntity.ok(couponService.createCoupon(requestDto));
    }

    @Operation(summary = "쿠폰 목록 조회", description = "쿠폰 목록을 페이징하여 조회함.") //현재 정렬 순서는 issuedQuantity, 이후에 검색어 or 정렬 기준 추가, 모든 데이터의 issuedQ가 0일때 조회 기준도 있어야함.
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponSimpleResponseDto>>> findAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ApiResponse<Page<CouponSimpleResponseDto>> response = couponReadService.findAllCoupons(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 상세 조회", description = "특정 쿠폰의 상세 정보를 조회함.")
    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponDetailResponseDto>> findCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal AuthUser authUser) {
        ApiResponse<CouponDetailResponseDto> response = couponReadService.findCoupon(couponId, authUser);
        return ResponseEntity.ok(response);
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
