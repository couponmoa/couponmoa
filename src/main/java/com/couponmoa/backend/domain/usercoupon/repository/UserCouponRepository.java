package com.couponmoa.backend.domain.usercoupon.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.repository.projection.UserCouponProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> {
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
}
