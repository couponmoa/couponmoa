package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.entity.QCoupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.store.entity.QStore;
import com.querydsl.core.types.OrderSpecifier;
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
    public Page<CouponSimpleResponse> searchCouponsByStore(Long storeId, String keyword, CouponStatus status,
                                                           BigDecimal discountAmount, BigDecimal discountRate,
                                                           LocalDateTime startDate, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;
        QStore store = QStore.store;

        List<CouponSimpleResponse> content = queryFactory
                .select(Projections.constructor(
                        CouponSimpleResponse.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountAmount,
                        coupon.discountRate
                ))
                .from(coupon)
                .join(coupon.store, store)
                .where(
                        storeIdEquals(storeId),
                        keywordContains(keyword),
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
                        keywordContains(keyword),
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
    public List<CouponSimpleResponse> searchCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size) {

        QCoupon coupon = QCoupon.coupon;

        // keyword가 있는 경우에만 필터링을 적용
        BooleanExpression keywordFilter = keywordContains(cursor.keyword());

        return queryFactory
                .select(Projections.constructor(
                        CouponSimpleResponse.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountAmount,
                        coupon.discountRate
                ))
                .from(coupon)
                .where(
                        coupon.deletedAt.isNull(),
                        couponStatusEq(status),
                        cursorFilter(cursor),
                        keywordFilter
                )
                .orderBy(orderSpecifiers())
                .limit(size)
                .fetch();
    }

    private BooleanExpression storeIdEquals(Long storeId) {
        return storeId == null ? null : QStore.store.id.eq(storeId);
    }

    private BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.isBlank()) ? null : QCoupon.coupon.name.containsIgnoreCase(keyword);
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

    private BooleanExpression cursorFilter(CouponCursor cursor) {
        if (cursor == null) return null;

        QCoupon coupon = QCoupon.coupon;

        BooleanExpression filter = null;

        // 조건 1: issuedQuantity 기준 (issuedQuantity가 있을 때만 적용)
        if (cursor.issuedQuantity() != null) {
            filter = coupon.issuedQuantity.lt(cursor.issuedQuantity());
        }

        // 조건 2: issuedQuantity 있고 keyword도 있을 때
        if (cursor.issuedQuantity() != null && cursor.keyword() != null) {
            BooleanExpression nameGt = coupon.issuedQuantity.eq(cursor.issuedQuantity().intValue())
                    .and(coupon.name.gt(cursor.keyword()));
            filter = (filter == null) ? nameGt : filter.or(nameGt);
        }

        // 조건 3: issuedQuantity, keyword, couponId 모두 있을 때
        if (cursor.issuedQuantity() != null && cursor.keyword() != null && cursor.couponId() != null) {
            BooleanExpression idLt = coupon.issuedQuantity.eq(cursor.issuedQuantity().intValue())
                    .and(coupon.name.eq(cursor.keyword()))
                    .and(coupon.id.lt(cursor.couponId()));
            filter = (filter == null) ? idLt : filter.or(idLt);
        }

        // 조건 4: issuedQuantity 또는 keyword만 있을 때, couponId를 고려하지 않도록 수정
        if (cursor.issuedQuantity() != null && cursor.keyword() != null && cursor.couponId() == null) {
            BooleanExpression issuedAndName = coupon.issuedQuantity.eq(cursor.issuedQuantity().intValue())
                    .and(coupon.name.eq(cursor.keyword()));
            filter = (filter == null) ? issuedAndName : filter.or(issuedAndName);
        }

        // 조건 5: keyword가 없으면 필터링을 아예 하지 않도록
        if (cursor.keyword() == null || cursor.keyword().isBlank()) {
            return filter;
        }

        return filter;
    }

    private OrderSpecifier<?>[] orderSpecifiers() {
        QCoupon coupon = QCoupon.coupon;

        return new OrderSpecifier[]{
                coupon.issuedQuantity.desc(),
                coupon.name.asc(),
                coupon.id.desc()
        };
    }
}
