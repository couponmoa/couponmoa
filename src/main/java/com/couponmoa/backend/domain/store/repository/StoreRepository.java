package com.couponmoa.backend.domain.store.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends BaseRepository<Store, Long> {

}
