package com.couponmoa.backend.domain.coupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.service.CouponReadService;
import com.couponmoa.backend.domain.coupon.service.CouponService;
import com.couponmoa.backend.domain.user.dto.AuthUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 API", description = "쿠폰 관련 기능을 제공하는 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;
	private final CouponReadService couponReadService;

	@Operation(summary = "쿠폰 생성", description = "관리자가 새로운 쿠폰을 생성함.")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
			@Valid @RequestBody CouponCreateRequest requestDto) {

		CouponResponse couponResponseDto = couponService.createCoupon(requestDto);
		return ResponseEntity.ok(ApiResponse.success(couponResponseDto));
	}

	@Operation(summary = "쿠폰 목록 조회", description = "쿠폰 목록을 페이징하여 조회함.")
	@GetMapping
	public ResponseEntity<ApiResponse<Page<CouponSimpleResponse>>> findAllCoupons(
			@Parameter(description = "조회할 페이지 번호 (1부터 시작)", example = "1")
			@RequestParam(defaultValue = "1") int page,
			@Parameter(description = "페이지당 쿠폰 수", example = "10")
			@RequestParam(defaultValue = "10") int size) {

		Page<CouponSimpleResponse> couponList = couponReadService.findAllCoupons(page, size);
		return ResponseEntity.ok(ApiResponse.success(couponList));
	}

	@Operation(summary = "쿠폰 상세 조회", description = "특정 쿠폰의 상세 정보를 조회함.")
	@GetMapping("/{couponId}")
	public ResponseEntity<ApiResponse<CouponDetailResponse>> findCoupon(
			@Parameter(description = "조회할 쿠폰의 ID", example = "5")
			@PathVariable Long couponId,
			@AuthenticationPrincipal AuthUser authUser) {

		CouponDetailResponse responseDto = couponReadService.findCoupon(couponId, authUser);
		return ResponseEntity.ok(ApiResponse.success(responseDto));
	}

	@Operation(summary = "쿠폰 수정", description = "관리자가 특정 쿠폰 정보를 수정함.")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{couponId}")
	public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
			@Parameter(description = "수정할 쿠폰의 ID", example = "5")
			@PathVariable Long couponId,
			@Valid @RequestBody CouponUpdateRequest requestDto) {

		CouponResponse couponResponseDto = couponService.updateCoupon(couponId, requestDto);
		return ResponseEntity.ok(ApiResponse.success(couponResponseDto));
	}

	@Operation(summary = "쿠폰 삭제", description = "관리자가 특정 쿠폰을 삭제함.")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{couponId}")
	public ResponseEntity<Void> deleteCoupon(
			@Parameter(description = "삭제할 쿠폰의 ID", example = "5")
			@PathVariable Long couponId) {

		couponService.deleteCoupon(couponId);
		return ResponseEntity.noContent().build();
	}
}
