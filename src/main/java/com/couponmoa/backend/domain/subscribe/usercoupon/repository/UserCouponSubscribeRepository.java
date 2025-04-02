package com.couponmoa.backend.domain.subscribe.usercoupon.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.subscribe.usercoupon.entity.UserCouponSubscribe;
import com.couponmoa.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserCouponSubscribeRepository extends BaseRepository<UserCouponSubscribe, Long> {
    Optional<UserCouponSubscribe> findByUserAndCoupon(User user, Coupon coupon);

    Page<UserCouponSubscribe> findByUser(User user, Pageable pageable);
}
