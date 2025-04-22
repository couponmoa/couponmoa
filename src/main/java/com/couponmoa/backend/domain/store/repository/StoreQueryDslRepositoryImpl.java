package com.couponmoa.backend.domain.store.repository;

import com.couponmoa.backend.domain.store.dto.request.StoreCursor;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.QStore;
import com.couponmoa.backend.domain.store.entity.Store;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

        BooleanExpression predicate = storeName != null && !storeName.isEmpty() ? store.name.containsIgnoreCase(storeName) : null;

        return queryFactory
                .selectFrom(store)
                .where(predicate)
                .fetch();
    }

    @Override
    public List<StoreResponse> searchStoresByKeyword(StoreCursor cursor, int size){
        QStore store = QStore.store;

        return queryFactory
                .select(Projections.constructor(
                        StoreResponse.class,
                        store.id,
                        store.name,
                        store.description,
                        store.address
                ))
                .from(store)
                .where(
                        store.deletedAt.isNull(),
                        cursorFilter(cursor)
                )
                .orderBy(orderSpecifiers(cursor))
                .limit(size)
                .fetch();
    }

    private BooleanExpression cursorFilter(StoreCursor cursor) {
        if (cursor == null || cursor.keyword() == null) return null;

        QStore store = QStore.store;

        // 이름을 기준으로, 이후 이름들만 가져오기
        return store.name.gt(cursor.keyword());
    }

    // Store 목록 조회 시 정렬 기준을 반환하는 메서드. 후속 요청: 이름을 기준으로 오름차순 정렬하며, 추가적으로 커서가 존재하면 후속 데이터를 가져올 수 있도록 처리합니다
    private OrderSpecifier<?>[] orderSpecifiers(StoreCursor cursor) {
        QStore store = QStore.store;

        if (cursor != null && cursor.keyword() != null) {
            // 후속 요청: 이름 기준으로 후속 데이터를 가져옴
            return new OrderSpecifier[]{
                    store.name.asc()  // 이름 기준 오름차순 정렬
            };
        } else {
            // 초기 요청: 이름 기준으로 오름차순 정렬
            return new OrderSpecifier[]{
                    store.name.asc()  // 이름 기준 오름차순 정렬
            };
        }
    }
}
