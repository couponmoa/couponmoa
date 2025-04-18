package com.couponmoa.backend.domain.coupon.service.v2;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponQueryDslRepository;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponReadServiceV2 {

    private final CouponQueryDslRepository couponQueryDslRepository;
    private final CouponRepository couponRepository;

    @Cacheable(value = "coupons", key = "T(com.couponmoa.backend.common.util.CacheKeyGenerator).generateCacheKey(#status, #cursor, #size)")
    @Retry(name = "couponService", fallbackMethod = "fallbackFindCouponsByKeyword")
    public List<CouponSimpleResponse> findCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size) {
        return couponQueryDslRepository.searchCouponsByKeyword(status, cursor, size);
    }

    @Cacheable(value = "coupons", key = "T(com.couponmoa.backend.common.util.CacheKeyGenerator).generateCacheKey(#storeId, #requestDto, #size, #page)")
    @Retry(name = "couponService", fallbackMethod = "fallbackFindCouponsByStore")
    public Page<CouponSimpleResponse> findCouponsByStore(
            Long storeId,
            CouponSearchByStoreRequest requestDto,
            int size, int page
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return couponQueryDslRepository.searchCouponsByStore(
                storeId,
                requestDto.getKeyword(),
                requestDto.getStatus(),
                requestDto.getDiscountAmount(),
                requestDto.getDiscountRate(),
                requestDto.getStartDate(),
                pageable
        );
    }

    @Cacheable(value = "couponDetails", key = "T(com.couponmoa.backend.common.util.CacheKeyGenerator).generateCouponCacheKey(#couponId)")
    @Retry(name = "couponService", fallbackMethod = "fallbackFindCoupon")
    public CouponDetailResponse findCoupon(Long couponId, AuthUser authUser) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));
        return CouponDetailResponse.toDto(coupon);
    }

    // findCouponsByKeyword 실패 시 fallback 메서드
    public List<CouponSimpleResponse> fallbackFindCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size, Exception e) {

        log.info("Redis 장애 발생, DB에서 조회: " + e.getMessage());

        return couponRepository.findAll()
                .stream()
                .map(CouponSimpleResponse::toDto)
                .collect(Collectors.toList());
    }

    // findCouponsByStore 실패 시 fallback 메서드
    public Page<CouponSimpleResponse> fallbackFindCouponsByStore(
            Long storeId,
            CouponSearchByStoreRequest requestDto,
            int size, int page, Exception e
    ) {
        log.info("Redis 장애 발생, DB에서 조회: " + e.getMessage());

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Coupon> couponPage = couponRepository.findByStoreId(storeId, pageable);

        return couponPage.map(CouponSimpleResponse::toDto);
    }

    // findCoupon 실패 시 fallback 메서드
    public CouponDetailResponse fallbackFindCoupon(Long couponId, AuthUser authUser, Exception e) {

        log.info("Redis 장애 발생, DB에서 조회: " + e.getMessage());

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        return CouponDetailResponse.toDto(coupon);
    }
}
