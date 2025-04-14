package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponReadServiceV2 {

    private final CouponQueryDslRepository couponQueryDslRepository;

    @Cacheable(
            value = "coupons",
            key = "T(com.couponmoa.backend.domain.coupon.util.CacheKeyGenerator).generateCacheKey(#status, #cursor, #size, #page)"
    )
    public List<CouponSimpleResponseDto> findCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size) {
        return couponQueryDslRepository.searchCouponsByKeyword(status, cursor, size);
    }

    @Cacheable(
            value = "coupons",
            key = "T(com.couponmoa.backend.domain.coupon.util.CacheKeyGenerator).generateCacheKey(#storeId, #requestDto, #size, #page)"
    )
    public Page<CouponSimpleResponseDto> findCouponsByStore(
            Long storeId,
            CouponSearchByStoreRequestDto requestDto,
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
}
