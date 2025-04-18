package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponReadService {

    private final CouponRepository couponRepository;

    public Page<CouponSimpleResponse> findAllCoupons(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Coupon> coupons = couponRepository.findAllSortedByIQ(pageable);
        return coupons.map(CouponSimpleResponse::toDto);
    }

    public CouponDetailResponse findCoupon(Long couponId, AuthUser authUser) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));
        return CouponDetailResponse.toDto(coupon);
    }
}
