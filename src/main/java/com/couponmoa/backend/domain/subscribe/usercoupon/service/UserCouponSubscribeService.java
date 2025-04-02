package com.couponmoa.backend.domain.subscribe.usercoupon.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.subscribe.usercoupon.entity.UserCouponSubscribe;
import com.couponmoa.backend.domain.subscribe.usercoupon.repository.UserCouponSubscribeRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.couponmoa.backend.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserCouponSubscribeService {

    private final UserRepository userRepo;
    private final CouponRepository couponRepo;
    private final UserCouponSubscribeRepository userCouponSubRepo;

    public Long subscribeCoupon(Long userId, Long couponId) {
        Coupon coupon = getCoupon(couponId);
        User user = getUser(userId);
        UserCouponSubscribe userCouponSubscribe = new UserCouponSubscribe(user, coupon);

        return userCouponSubRepo.save(userCouponSubscribe).getId();
    }

    public void unSubscribeCoupon(Long userId, Long couponId) {
        Coupon coupon = getCoupon(couponId);
        User user = getUser(userId);


    }

    private User getUser(Long userId) {
        return userRepo.findByIdOrElseThrow(userId, NOT_FOUNT_USER);
    }

    private Coupon getCoupon(Long couponId) {
        return couponRepo.findByIdOrElseThrow(couponId, NOT_FOUNT_COUPON);
    }

    private UserCouponSubscribe findById(Long id) {
        return userCouponSubRepo.findByIdOrElseThrow(id, NOT_FOUNT_USER_COUPON);
    }


}
