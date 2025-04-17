package com.couponmoa.backend.domain.usercoupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCoupon is a Querydsl query type for UserCoupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCoupon extends EntityPathBase<UserCoupon> {

    private static final long serialVersionUID = -1008417171L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCoupon userCoupon = new QUserCoupon("userCoupon");

    public final com.couponmoa.backend.common.entity.QBaseEntity _super = new com.couponmoa.backend.common.entity.QBaseEntity(this);

    public final StringPath code = createString("code");

    public final com.couponmoa.backend.domain.coupon.entity.QCoupon coupon;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final EnumPath<com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus> status = createEnum("status", com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus.class);

    public final com.couponmoa.backend.domain.user.entity.QUser user;

    public QUserCoupon(String variable) {
        this(UserCoupon.class, forVariable(variable), INITS);
    }

    public QUserCoupon(Path<? extends UserCoupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCoupon(PathMetadata metadata, PathInits inits) {
        this(UserCoupon.class, metadata, inits);
    }

    public QUserCoupon(Class<? extends UserCoupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new com.couponmoa.backend.domain.coupon.entity.QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
        this.user = inits.isInitialized("user") ? new com.couponmoa.backend.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

