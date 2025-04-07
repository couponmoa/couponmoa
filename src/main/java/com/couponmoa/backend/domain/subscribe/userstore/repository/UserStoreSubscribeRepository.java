package com.couponmoa.backend.domain.subscribe.userstore.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.subscribe.userstore.entity.UserStoreSubscribe;
import com.couponmoa.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface UserStoreSubscribeRepository extends BaseRepository<UserStoreSubscribe, Long> {
    Optional<UserStoreSubscribe> findByUserAndStore(User user, Store store);

    List<UserStoreSubscribe> findByStore_Id(Long storeId);

    List<UserStoreSubscribe> findByUser(User user);

    @EntityGraph(attributePaths = {"user"})
    Page<UserStoreSubscribe> findByUser(User user, Pageable pageable);

    boolean existsByUserAndStore(User user, Store store);
}
