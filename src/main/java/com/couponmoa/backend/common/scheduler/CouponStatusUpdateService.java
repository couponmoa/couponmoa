package com.couponmoa.backend.common.scheduler;

import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponStatusUpdateService {

    private final UserCouponRepository userCouponRepository;

    @Recurring(id = "coupon-expire-job", cron = "0 0 * * *")
    @Job(name = "Update the status of expired user coupons to ‘expired’", retries = 3)
    @Transactional
    public void updateCouponStatusExpired() {
        userCouponRepository.updateCouponStatusExpired();
    }
}
