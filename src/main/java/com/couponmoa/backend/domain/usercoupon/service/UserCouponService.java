package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.usercoupon.dto.request.UserCouponRequest;
import com.couponmoa.backend.domain.usercoupon.dto.response.UseUserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponCodeResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserCouponRedisService userCouponRedisService;
    private final UserCouponAsyncService userCouponAsyncService;

    @Timed(value = "user_coupon.create_sync.time", description = "동기 쿠폰 발급에 걸린 시간",  histogram = true)
    @Counted(value = "user_coupon.create_sync.count", description = "동기 쿠폰 발급 횟수")
    public void createUserCouponSync(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findActiveByIdOrElseThrow(couponId, ErrorCode.COUPON_NOT_FOUND);

        validateCouponIssuablePeriod(coupon.getStatus());
        validateCouponNotSoldOut(coupon.getAvailableQuantity());

        Integer resultCode = userCouponRedisService.couponIssue(userId, couponId);
        validateIssueResultCode(resultCode);

        userCouponAsyncService.saveUserCoupon(userId, couponId);
    }

    @Timed(value = "user_coupon.create_async.time", description = "비동기 쿠폰 발급 요청에 걸린 시간",  histogram = true)
    @Counted(value = "user_coupon.create_async.count", description = "비동기 쿠폰 발급 요청 횟수")
    public void createUserCouponAsync(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findActiveByIdOrElseThrow(couponId, ErrorCode.COUPON_NOT_FOUND);

        validateCouponIssuablePeriod(coupon.getStatus());
        validateCouponNotSoldOut(coupon.getAvailableQuantity());

        userCouponAsyncService.couponIssue(userId, coupon);
    }

    @Timed(value = "user_coupon.find_coupons.time", description = "사용자 쿠폰 목록 조회에 걸린 시간",  histogram = true)
    @Counted(value = "user_coupon.find_coupons.count", description = "사용자 쿠폰 목록 조회 횟수")
    @Transactional(readOnly = true)
    public Page<UserCouponResponse> findUserCoupons(Long userId, UserCouponStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return userCouponRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(UserCouponResponse::from);
    }

    @Timed(value = "user_coupon.find_code.time", description = "쿠폰 코드 조회에 걸린 시간",  histogram = true)
    @Counted(value = "user_coupon.find_code.count", description = "쿠폰 코드 조회 횟수")
    @Transactional(readOnly = true)
    public UserCouponCodeResponse findUserCouponCode(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findByIdOrElseThrow(userCouponId, ErrorCode.USER_COUPON_NOT_FOUND);

        validateCouponOwner(userCoupon.getUser(), userId);
        validateCouponStatus(userCoupon.getStatus());

        return new UserCouponCodeResponse(userCoupon.getCode());
    }

    @Timed(value = "user_coupon.use.time", description = "쿠폰 사용 처리에 걸린 시간",  histogram = true)
    @Counted(value = "user_coupon.use.count", description = "쿠폰 사용 처리 횟수")
    @Transactional
    public UseUserCouponResponse useUserCoupon(Long userId, UserCouponRequest request) {
        UserCoupon userCoupon = userCouponRepository.findByCodeWithCouponAndStore(request.getCode())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_COUPON_NOT_FOUND));

        validateCouponStoreOwner(userCoupon.getCoupon().getStore(), userId);
        validateCouponStatus(userCoupon.getStatus());

        userCoupon.setUsed();
        return UseUserCouponResponse.from(userCoupon);
    }

    private void validateCouponIssuablePeriod(CouponStatus status) {
        if (status != CouponStatus.IN_PROGRESS) {
            throw new ApplicationException(ErrorCode.COUPON_NOT_ACTIVE);
        }
    }

    private void validateCouponNotSoldOut(int availableQuantity) {
        if (availableQuantity <= 0) {
            throw new ApplicationException(ErrorCode.COUPON_SOLD_OUT);
        }
    }

    private void validateIssueResultCode(Integer resultCode) {
        switch (resultCode) {
            case 0:
                return;
            case 1:
                throw new IllegalStateException("쿠폰 재고가 redis에 등록되지 않았습니다.");
            case 2:
                throw new ApplicationException(ErrorCode.DUPLICATED_USER_COUPON);
            case 3:
                throw new ApplicationException(ErrorCode.COUPON_SOLD_OUT);
            default:
                throw new IllegalStateException("예상하지 못한 값이 반환되었습니다.");
        }
    }

    private void validateCouponOwner(User userCouponOwner, Long userId) {
        if (!userCouponOwner.getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.USER_COUPON_ACCESS_DENIED);
        }
    }

    private void validateCouponStatus(UserCouponStatus status) {
        if (status != UserCouponStatus.UNUSED) {
            throw new ApplicationException(ErrorCode.USER_COUPON_CODE_UNAVAILABLE);
        }
    }

    private void validateCouponStoreOwner(Store store, Long userId) {
        if (!store.getUser().getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.USER_COUPON_ACCESS_DENIED);
        }
    }
}
