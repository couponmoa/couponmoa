package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.entity.QCoupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.store.entity.QStore;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponQueryDslRepositoryImpl implements CouponQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    // 조회 요청에 storeId값이 있을 때 쿠폰 목록 조회, store별로 쿠폰은 많지 않을 것이기 때문에, offset 방식
    @Override
    public Page<CouponSimpleResponseDto> searchCouponsByStore(Long storeId, String keyword, CouponStatus status,
                                                       BigDecimal discountAmount, BigDecimal discountRate,
                                                       LocalDateTime startDate, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;
        QStore store = QStore.store;

        List<CouponSimpleResponseDto> content = queryFactory
                .select(Projections.constructor(
                        CouponSimpleResponseDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountAmount,
                        coupon.discountRate
                ))
                .from(coupon)
                .join(coupon.store, store)
                .where(
                        storeIdEquals(storeId),
                        couponNameContains(keyword),
                        couponStatusEq(status),
                        couponDiscountAmountEq(discountAmount),
                        couponDiscountRateEq(discountRate),
                        couponStartDateAfter(startDate),
                        coupon.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(coupon.issuedQuantity.desc(), coupon.name.asc())
                .fetch();

        // 조건에 해당하는 쿠폰의 전체 개수
        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(coupon)
                .join(coupon.store, store)
                .where(
                        storeIdEquals(storeId),
                        couponNameContains(keyword),
                        couponStatusEq(status),
                        couponDiscountAmountEq(discountAmount),
                        couponDiscountRateEq(discountRate),
                        couponStartDateAfter(startDate),
                        coupon.deletedAt.isNull()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);
    }

    // storeId가 없을때 keyword로 조회 (keyword는 없을수도, 그럼 issuedQuantity와 name 기준 정렬, 전체 쿠폰 조회), cursor방식)
    @Override
    public List<CouponSimpleResponseDto> searchCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size) {

        QCoupon coupon = QCoupon.coupon;

        return queryFactory
                .select(Projections.constructor(
                        CouponSimpleResponseDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountAmount,
                        coupon.discountRate
                ))
                .from(coupon)
                .where(
                        coupon.deletedAt.isNull(),
                        couponStatusEq(status),
                        cursorFilter(cursor)
                )
                .orderBy(
                        coupon.issuedQuantity.desc(),
                        coupon.name.asc(),
                        coupon.id.desc()
                )
                .limit(size)
                .fetch();
    }

    private BooleanExpression storeIdEquals(Long storeId) {
        return storeId == null ? null : QStore.store.id.eq(storeId);
    }

    private BooleanExpression couponNameContains(String keyword) {
        return keyword == null || keyword.isBlank() ? null : QCoupon.coupon.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression couponStatusEq(CouponStatus status) {
        return status == null ? null : QCoupon.coupon.status.eq(status);
    }

    private BooleanExpression couponDiscountAmountEq(BigDecimal discountAmount) {
        return discountAmount == null ? null : QCoupon.coupon.discountAmount.eq(discountAmount);
    }

    private BooleanExpression couponDiscountRateEq(BigDecimal discountRate) {
        return discountRate == null ? null : QCoupon.coupon.discountRate.eq(discountRate);
    }

    private BooleanExpression couponStartDateAfter(LocalDateTime startDate) {
        return startDate == null ? null : QCoupon.coupon.startDate.after(startDate);
    }

    // 현재 요청값으로 전달된 커서 이후의 데이터만 핕터링
    private BooleanExpression cursorFilter(CouponCursor cursor) {
        if (cursor == null) return null;

        QCoupon coupon = QCoupon.coupon;

        NumberExpression<BigDecimal> issuedQuantityExpression = Expressions.numberTemplate(BigDecimal.class, "{0}", coupon.issuedQuantity);

        return issuedQuantityExpression.lt(cursor.issuedQuantity())
                .or(
                        issuedQuantityExpression.eq(cursor.issuedQuantity())
                                .and(coupon.name.gt(cursor.keyword()))
                )
                .or(
                        issuedQuantityExpression.eq(cursor.issuedQuantity())
                                .and(coupon.name.eq(cursor.keyword()))
                                .and(coupon.id.lt(cursor.couponId()))
                );
    }
}
