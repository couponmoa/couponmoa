package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import com.couponmoa.backend.domain.usercoupon.dto.request.UserCouponRequest;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponCodeResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UseUserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void createUserCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findActiveByIdOrElseThrow(couponId, ErrorCode.COUPON_NOT_FOUND);

        validateCouponIssuable(coupon);
        validateCouponNotAlreadyIssued(userId, couponId);

        coupon.availableQuantityDown();

        couponRepository.flush();

        User user = userRepository.getReferenceById(userId);
        UserCoupon userCoupon = new UserCoupon(user, coupon);
        userCouponRepository.save(userCoupon);
    }

    public Page<UserCouponResponse> findUserCoupons(Long userId, UserCouponStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return userCouponRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(UserCouponResponse::from);
    }

    public UserCouponCodeResponse findUserCouponCode(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findByIdOrElseThrow(userCouponId, ErrorCode.USER_COUPON_NOT_FOUND);

        validateCouponOwner(userCoupon.getUser(), userId);
        validateCouponStatus(userCoupon.getStatus());

        return new UserCouponCodeResponse(userCoupon.getCode());
    }

    @Transactional
    public UseUserCouponResponse useUserCoupon(Long userId, UserCouponRequest request) {
        UserCoupon userCoupon = userCouponRepository.findByCodeWithCouponAndStore(request.getCode())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_COUPON_NOT_FOUND));

        validateCouponStoreOwner(userCoupon.getCoupon().getStore(), userId);
        validateCouponStatus(userCoupon.getStatus());

        userCoupon.setUsed();
        return UseUserCouponResponse.from(userCoupon);
    }

    private void validateCouponIssuable(Coupon coupon) {
        CouponStatus currentStatus = CouponStatus.editStatus(
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getAvailableQuantity()
        );
        if (currentStatus != CouponStatus.IN_PROGRESS) {
            throw new ApplicationException(ErrorCode.COUPON_NOT_ACTIVE);
        }
    }

    private void validateCouponNotAlreadyIssued(Long userId, Long couponId) {
        Boolean alreadyIssued = userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
        if (alreadyIssued) {
            throw new ApplicationException(ErrorCode.USER_COUPON_ALREADY_ISSUED);
        }
    }

    private static void validateCouponOwner(User userCouponOwner, Long userId) {
        if (!userCouponOwner.getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.USER_COUPON_ACCESS_DENIED);
        }
    }

    private void validateCouponStatus(UserCouponStatus status) {
        if (status != UserCouponStatus.UNUSED) {
            throw new ApplicationException(ErrorCode.USER_COUPON_CODE_UNAVAILABLE);
        }
    }

    private static void validateCouponStoreOwner(Store store, Long userId) {
        if (!store.getUser().getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.USER_COUPON_ACCESS_DENIED);
        }
    }
}
