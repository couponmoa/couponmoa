package com.couponmoa.backend.domain.store.repository;

import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.dto.request.StoreCursor;

import java.util.List;

public interface StoreQueryDslRepository {

    List<Store> findAllStoreByName(String storeName);

    List<StoreResponse> searchStoresByKeyword(StoreCursor cursor, int size);
}
