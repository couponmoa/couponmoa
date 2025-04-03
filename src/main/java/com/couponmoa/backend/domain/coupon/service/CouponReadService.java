package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponReadService {

    private final CouponRepository couponRepository;

    public ApiResponse<Page<CouponSimpleResponseDto>> findAllCoupons(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Coupon> coupons = couponRepository.findAllSortedByIssuedQuantity(pageable);

        //테스트 로깅
        log.info("조회된 쿠폰 개수: {}",coupons.getTotalElements());
        for (Coupon coupon : coupons) {
            log.info("쿠폰 id: {}, 쿠폰 이름: {}", coupon.getId(), coupon.getName());
        }

        return ApiResponse.success(coupons.map(CouponSimpleResponseDto::toDto));
    }

    public ApiResponse<CouponDetailResponseDto> findCoupon(Long couponId, AuthUser authUser) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        return ApiResponse.success(CouponDetailResponseDto.toDto(coupon));
    }
}
