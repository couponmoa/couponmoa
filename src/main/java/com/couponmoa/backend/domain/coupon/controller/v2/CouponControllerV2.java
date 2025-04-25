package com.couponmoa.backend.domain.coupon.controller.v2;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.service.v2.CouponReadServiceV2;
import com.couponmoa.backend.domain.coupon.service.v2.CouponServiceV2;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "쿠폰 API V2", description = "쿠폰 관련 기능을 제공하는 API + 캐싱")
@RestController
@RequestMapping("/api/v2/coupons")
@RequiredArgsConstructor
@Slf4j
public class CouponControllerV2 {

    private final CouponReadServiceV2 couponReadServiceV2;
    private final CouponServiceV2 couponServiceV2;

    @Operation(summary = "쿠폰 생성", description = "관리자가 새로운 쿠폰을 생성함 (캐시 무효화 포함).")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CouponCreateRequest requestDto) {

        CouponResponse responseDto = couponServiceV2.createCoupon(requestDto);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "키워드로 쿠폰 조회 (커서 방식)", description = "키워드로 쿠폰을 조회하고, 커서 방식으로 페이징 처리함")
    @GetMapping
    public ApiResponse<List<CouponSimpleResponse>> findCouponsByKeyword(
            @RequestParam(required = false) CouponStatus status,
            @RequestParam(required = false) BigDecimal issuedQuantity,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "10") int size) {

        CouponCursor cursor = (issuedQuantity != null || keyword != null || id != null)
                ? new CouponCursor(issuedQuantity, keyword, id)
                : null;

        List<CouponSimpleResponse> coupons = couponReadServiceV2.findCouponsByKeyword(status, cursor, size);
        return ApiResponse.success(coupons);
    }

    @Operation(summary = "스토어별 쿠폰 조회", description = "스토어 ID로 해당 스토어에 대한 쿠폰 목록을 조회함")
    @GetMapping("/store/{storeId}")
    public ApiResponse<Page<CouponSimpleResponse>> findCouponsByStore(
            @PathVariable Long storeId,
            @ModelAttribute CouponSearchByStoreRequest requestDto,
            @Parameter(description = "페이지당 쿠폰 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "조회할 페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page) {

        Page<CouponSimpleResponse> coupons = couponReadServiceV2.findCouponsByStore(storeId, requestDto, size, page);
        return ApiResponse.success(coupons);
    }

    @Operation(summary = "특정 쿠폰 상세 정보 조회", description = "쿠폰 ID를 통해 특정 쿠폰의 상세 정보를 조회함 (캐싱 적용)")
    @GetMapping("/{couponId}")
    public ApiResponse<CouponDetailResponse> findCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal AuthUser authUser) {

        CouponDetailResponse couponDetail = couponReadServiceV2.findCoupon(couponId, authUser);
        return ApiResponse.success(couponDetail);
    }

    @Operation(summary = "쿠폰 수정", description = "관리자가 특정 쿠폰을 수정함 (캐시 무효화 포함).")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponUpdateRequest requestDto) {

        CouponResponse responseDto = couponServiceV2.updateCoupon(couponId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "쿠폰 삭제", description = "관리자가 특정 쿠폰을 삭제함 (캐시 무효화 포함).")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(
            @PathVariable Long couponId) {

        couponServiceV2.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}
