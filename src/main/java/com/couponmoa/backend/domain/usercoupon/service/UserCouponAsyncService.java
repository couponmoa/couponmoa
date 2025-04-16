package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.notification.service.ExpiredNotificationService;
import com.couponmoa.backend.domain.notification.service.IssuedNotificationService;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponAsyncService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserCouponRedisService userCouponRedisService;
    private final IssuedNotificationService issuedNotificationService;
    private final ExpiredNotificationService expiredNotificationService;

    @Async
    public void saveUserCoupon(Long userId, Long couponId) {
        User user = userRepository.getReferenceById(userId);
        Coupon coupon = couponRepository.getReferenceById(couponId);
        UserCoupon userCoupon = new UserCoupon(user, coupon);
        userCouponRepository.save(userCoupon);
    }

    @Async
    public void couponIssue(Long userId, Long couponId) {
        Integer resultCode = userCouponRedisService.couponIssue(userId, couponId);

        if (resultCode == 0) { // 쿠폰 발급 성공
            saveUserCoupon(userId, couponId);
            saveNotification(userId, userCoupon);
        }
    }

    private void saveNotification(Long userId, UserCoupon userCoupon) {
        issuedNotificationService.createIssuedNotification(userId, userCoupon);
        expiredNotificationService.createCouponExpireNotification(userCoupon);
    }
}
