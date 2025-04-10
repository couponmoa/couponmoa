package com.couponmoa.backend.common.scheduler;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import com.couponmoa.backend.domain.usercoupon.service.CouponRedisService;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponStatusUpdateService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final CouponRedisService couponRedisService;

    @Recurring(id = "coupon-expire-job", cron = "0 0 * * *")
    @Job(name = "Update the status of expired user coupons to ‘expired’", retries = 3)
    @Transactional
    public void expireUserCoupons() {
        userCouponRepository.expireUnusedUserCoupons();
    }

    @Recurring(id = "coupon-activate-job", cron = "0 * * * *")
    @Job(name = "Update the status of coupons to ‘in_progress’ at issuance start time", retries = 3)
    @Transactional
    public void activateAvailableCoupons() {
        List<Coupon> coupons = couponRepository.findCouponsToActivate();
        for (Coupon coupon : coupons) {
            coupon.updateStatus(CouponStatus.IN_PROGRESS);
            couponRedisService.saveStock(coupon);
        }
    }

    @Recurring(id = "coupon-end-job", cron = "0 * * * *")
    @Job(name = "Update the status of coupons to ‘ended’ after issuance period ends", retries = 3)
    @Transactional
    public void endActiveCoupons() {
        List<Coupon> coupons = couponRepository.findCouponsToEnd();
        for (Coupon coupon : coupons) {
            coupon.updateStatus(CouponStatus.ENDED);
            couponRedisService.deleteUserSet(coupon.getId());
        }
    }
}