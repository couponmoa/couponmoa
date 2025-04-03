package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponReadService {

    private final CouponRepository couponRepository;

    public ApiResponse<Page<CouponSimpleResponseDto>> findCoupons() {
        return null;
    }

    public ApiResponse<CouponDetailResponseDto> findCoupon(Long couponId, AuthUser authUser) {

        return null;
    }
}
