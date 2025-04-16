package com.couponmoa.backend.domain.store.repository;

import com.couponmoa.backend.domain.store.entity.Store;

import java.util.List;

public interface StoreQueryDslRepository {

    public List<Store> findAllStoreByName(String storeName);
}
