package com.couponmoa.backend.domain.usercoupon.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.usercoupon.repository.projection.UserCouponProjection;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> {
    Boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @Query("""
                SELECT uc.id AS id, uc.status AS status, c.discountAmount AS discountAmount, c.discountRate AS discountRate,
                       c.name AS name, c.description AS description, c.expiryDate AS expiryDate, c.minOrderAmount AS minOrderAmount,
                       c.maxDiscountAmount AS maxDiscountAmount
                FROM UserCoupon uc
                JOIN uc.coupon c
                WHERE uc.user.id = :userId AND (:status IS NULL OR uc.status = :status)
            """)
    Page<UserCouponProjection> findByUserIdAndStatus(Long userId, UserCouponStatus status, Pageable pageable);

    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon c JOIN FETCH c.store s WHERE uc.code = :code")
    Optional<UserCoupon> findByCodeWithCouponAndStore(String code);

    @Modifying
    @Query("""
                 UPDATE UserCoupon uc SET uc.status = 'EXPIRED', uc.modifiedAt = CURRENT_TIMESTAMP
                 WHERE uc.status = 'UNUSED'
                 AND uc.coupon.id IN (SELECT c.id FROM Coupon c WHERE c.expiryDate <= CURRENT_TIMESTAMP)
            """)
    void expireUnusedUserCoupons();
}
