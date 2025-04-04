package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponRepository extends BaseRepository<Coupon,Long> {
    @Override
    default Coupon findByIdOrElseThrow(Long aLong, ErrorCode errorCode) {
        return BaseRepository.super.findByIdOrElseThrow(aLong, errorCode);
    }

    @Query("SELECT c FROM Coupon c ORDER BY c.issuedQuantity DESC, c.name ASC")
    Page<Coupon> findAllSortedByIssuedQuantity(Pageable pageable);

    Optional<Coupon> findByIdAndDeletedAtIsNull(Long id);

    default Coupon findActiveByIdOrElseThrow(Long id, ErrorCode errorCode) {
        return findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApplicationException(errorCode, errorCode.getMessage() + " id = " + id));
    }
}
