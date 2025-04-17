package com.couponmoa.backend.domain.subscribe.usercoupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCouponSubscribe is a Querydsl query type for UserCouponSubscribe
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCouponSubscribe extends EntityPathBase<UserCouponSubscribe> {

    private static final long serialVersionUID = 1221920057L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCouponSubscribe userCouponSubscribe = new QUserCouponSubscribe("userCouponSubscribe");

    public final com.couponmoa.backend.common.entity.QBaseEntity _super = new com.couponmoa.backend.common.entity.QBaseEntity(this);

    public final com.couponmoa.backend.domain.coupon.entity.QCoupon coupon;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.couponmoa.backend.domain.user.entity.QUser user;

    public QUserCouponSubscribe(String variable) {
        this(UserCouponSubscribe.class, forVariable(variable), INITS);
    }

    public QUserCouponSubscribe(Path<? extends UserCouponSubscribe> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCouponSubscribe(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCouponSubscribe(PathMetadata metadata, PathInits inits) {
        this(UserCouponSubscribe.class, metadata, inits);
    }

    public QUserCouponSubscribe(Class<? extends UserCouponSubscribe> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new com.couponmoa.backend.domain.coupon.entity.QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
        this.user = inits.isInitialized("user") ? new com.couponmoa.backend.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

