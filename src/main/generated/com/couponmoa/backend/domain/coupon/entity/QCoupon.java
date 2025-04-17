package com.couponmoa.backend.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -792871049L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final com.couponmoa.backend.common.entity.QBaseEntity _super = new com.couponmoa.backend.common.entity.QBaseEntity(this);

    public final NumberPath<Integer> availableQuantity = createNumber("availableQuantity", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<java.math.BigDecimal> discountAmount = createNumber("discountAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> discountRate = createNumber("discountRate", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> expiryDate = createDateTime("expiryDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> issuedQuantity = createNumber("issuedQuantity", Integer.class);

    public final NumberPath<java.math.BigDecimal> maxDiscountAmount = createNumber("maxDiscountAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minOrderAmount = createNumber("minOrderAmount", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final EnumPath<com.couponmoa.backend.domain.coupon.enums.CouponStatus> status = createEnum("status", com.couponmoa.backend.domain.coupon.enums.CouponStatus.class);

    public final com.couponmoa.backend.domain.store.entity.QStore store;

    public final NumberPath<Integer> totalQuantity = createNumber("totalQuantity", Integer.class);

    public QCoupon(String variable) {
        this(Coupon.class, forVariable(variable), INITS);
    }

    public QCoupon(Path<? extends Coupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoupon(PathMetadata metadata, PathInits inits) {
        this(Coupon.class, metadata, inits);
    }

    public QCoupon(Class<? extends Coupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.couponmoa.backend.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

