package com.couponmoa.backend.domain.store.repository;

import com.couponmoa.backend.domain.store.entity.QStore;
import com.couponmoa.backend.domain.store.entity.Store;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreQueryDslRepositoryImpl implements StoreQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> findAllStoreByName(String storeName) {
        QStore store = QStore.store;
        BooleanBuilder builder = new BooleanBuilder();

        if (storeName != null && !storeName.isEmpty()) {
            builder.and(store.name.containsIgnoreCase(storeName));
        }

        return queryFactory
                .selectFrom(store)
                .where(builder)
                .fetch();
    }
}
