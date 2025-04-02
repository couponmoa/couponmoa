package com.couponmoa.backend.domain.usercoupon.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;

public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> {
    Boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}
