package com.couponmoa.backend.domain.subscribe.usercoupon.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.subscribe.usercoupon.entity.UserCouponSubscribe;
import com.couponmoa.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface UserCouponSubscribeRepository extends BaseRepository<UserCouponSubscribe, Long> {
    Optional<UserCouponSubscribe> findByUserAndCoupon(User user, Coupon coupon);

    Page<UserCouponSubscribe> findByUser(User user, Pageable pageable);

    boolean existsByUserAndCoupon(User user, Coupon coupon);

    @EntityGraph(attributePaths = {"user"})
    List<UserCouponSubscribe> findByCoupon_Id(Long couponId);
}
