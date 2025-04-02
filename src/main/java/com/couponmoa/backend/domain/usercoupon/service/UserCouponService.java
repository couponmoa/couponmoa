package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void createUserCoupon(Long userId, Long couponId) {
        // TODO: 쿠폰에 deleted_at 추가될 경우 삭제되지 않은 쿠폰만 조회하도록 수정
        Coupon coupon = couponRepository.findByIdOrElseThrow(couponId, ErrorCode.COUPON_NOT_FOUND);

        validateCouponIssuable(coupon);
        validateCouponNotAlreadyIssued(userId, couponId);

        coupon.availableQuantityDown();
        couponRepository.flush();

        User user = userRepository.getReferenceById(userId);
        UserCoupon userCoupon = new UserCoupon(user, coupon);
        userCouponRepository.save(userCoupon);
    }

    private void validateCouponIssuable(Coupon coupon) {
        validateCouponActivePeriod(coupon.getStartDate(), coupon.getEndDate());
        validateCouponAvailableQuantity(coupon.getAvailableQuantity());
    }

    private void validateCouponActivePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isAfter(now) || endDate.isBefore(now)) {
            throw new ApplicationException(ErrorCode.COUPON_NOT_ACTIVE);
        }
    }

    private void validateCouponAvailableQuantity(Integer availableQuantity) {
        if (availableQuantity <= 0) {
            throw new ApplicationException(ErrorCode.COUPON_SOLE_OUT);
        }
    }

    private void validateCouponNotAlreadyIssued(Long userId, Long couponId) {
        Boolean alreadyIssued = userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
        if (alreadyIssued) {
            throw new ApplicationException(ErrorCode.COUPON_ALREADY_ISSUED);
        }
    }
}
