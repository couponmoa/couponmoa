package com.couponmoa.backend.domain.subscribe.userstore.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserStoreSubscribe is a Querydsl query type for UserStoreSubscribe
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserStoreSubscribe extends EntityPathBase<UserStoreSubscribe> {

    private static final long serialVersionUID = 903892149L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserStoreSubscribe userStoreSubscribe = new QUserStoreSubscribe("userStoreSubscribe");

    public final com.couponmoa.backend.common.entity.QBaseEntity _super = new com.couponmoa.backend.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.couponmoa.backend.domain.store.entity.QStore store;

    public final com.couponmoa.backend.domain.user.entity.QUser user;

    public QUserStoreSubscribe(String variable) {
        this(UserStoreSubscribe.class, forVariable(variable), INITS);
    }

    public QUserStoreSubscribe(Path<? extends UserStoreSubscribe> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserStoreSubscribe(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserStoreSubscribe(PathMetadata metadata, PathInits inits) {
        this(UserStoreSubscribe.class, metadata, inits);
    }

    public QUserStoreSubscribe(Class<? extends UserStoreSubscribe> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.couponmoa.backend.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
        this.user = inits.isInitialized("user") ? new com.couponmoa.backend.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

