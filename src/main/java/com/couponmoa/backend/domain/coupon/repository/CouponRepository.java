package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.coupon.entity.Coupon;

public interface CouponRepository extends BaseRepository<Coupon,Long> {
    @Override
    default Coupon findByIdOrElseThrow(Long aLong, ErrorCode errorCode) {
        return BaseRepository.super.findByIdOrElseThrow(aLong, errorCode);
    }
}
