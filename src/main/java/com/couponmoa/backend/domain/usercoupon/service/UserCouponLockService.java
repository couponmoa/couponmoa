package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.common.annotation.DistributedLock;
import com.couponmoa.backend.common.annotation.LockKey;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponLockService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private static final String LOCK_KEY_PREFIX = "coupon-issue-lock";

    @DistributedLock(LOCK_KEY_PREFIX)
    @Transactional
    public void updateCouponWithLock(Long userId, @LockKey Long couponId) {
        Coupon coupon = couponRepository.findActiveByIdOrElseThrow(couponId, ErrorCode.COUPON_NOT_FOUND);

        validateCouponIssuable(coupon.getStatus());
        validateCouponNotAlreadyIssued(userId, couponId);

        coupon.availableQuantityDown();
        couponRepository.flush();
    }

    private void validateCouponIssuable(CouponStatus status) {
        if (status == CouponStatus.SOLD_OUT) {
            throw new ApplicationException(ErrorCode.COUPON_SOLD_OUT);
        }

        if (status != CouponStatus.IN_PROGRESS) {
            throw new ApplicationException(ErrorCode.COUPON_NOT_ACTIVE);
        }
    }

    private void validateCouponNotAlreadyIssued(Long userId, Long couponId) {
        Boolean alreadyIssued = userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
        if (alreadyIssued) {
            throw new ApplicationException(ErrorCode.USER_COUPON_ALREADY_ISSUED);
        }
    }
}
