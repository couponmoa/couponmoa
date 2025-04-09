package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends BaseRepository<Coupon,Long> {
    @Override
    default Coupon findByIdOrElseThrow(Long aLong, ErrorCode errorCode) {
        return BaseRepository.super.findByIdOrElseThrow(aLong, errorCode);
    }

    // 모든 CouponCategory의 쿠폰 조회 (deletedAt 제외)
    @Query("SELECT c FROM Coupon c WHERE c.deletedAt IS NULL " +
            "ORDER BY c.issuedQuantity DESC, c.name ASC")
    Page<Coupon> findAllSortedByIQ(Pageable pageable);

    // CouponCategory에 따라 목록 조회 (deletedAt 제외)
    @Query("SELECT c FROM Coupon c WHERE c.status = :category AND c.deletedAt IS NULL " +
            "ORDER BY c.issuedQuantity DESC, c.name ASC")
    Page<Coupon> findAllByStatusSortedByIQ(@Param("status") CouponStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Coupon> findActiveByIdForUpdate(Long id);
}
