package com.couponmoa.backend.domain.subscribe.userstore.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.subscribe.userstore.dto.response.FindStoreSubscribeListResponse;
import com.couponmoa.backend.domain.subscribe.userstore.entity.UserStoreSubscribe;
import com.couponmoa.backend.domain.subscribe.userstore.repository.UserStoreSubscribeRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.couponmoa.backend.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStoreSubscribeService {

    private final UserRepository userRepo;
    private final StoreRepository storeRepo;
    private final UserStoreSubscribeRepository userStoreSubRepo;

    public void subscribeStore(Long userId, Long storeId) {

        Store store = getStore(storeId);
        User user = getUser(userId);

        if (userStoreSubRepo.existsByUserAndStore(user, store)) {
            throw new ApplicationException(DUPLICATED_USER_COUPON);
        }

        UserStoreSubscribe userCouponSubscribe = new UserStoreSubscribe(user, store);
        userStoreSubRepo.save(userCouponSubscribe).getId();
    }

    public void unSubscribeCoupon(Long userId, Long storeId) {
        Store store = getStore(storeId);
        User user = getUser(userId);

        UserStoreSubscribe userCouponSubscribe = userStoreSubRepo.findByUserAndStore(user, store).orElseThrow(() -> new ApplicationException(NOT_FOUND_STORE));

        userStoreSubRepo.delete(userCouponSubscribe);
    }

    public List<FindStoreSubscribeListResponse> findSubscribeList(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        User user = getUser(userId);

        return userStoreSubRepo.findByUser(user, pageable)
                .stream()
                .map(FindStoreSubscribeListResponse::new)
                .toList();
    }

    private User getUser(Long userId) {
        return userRepo.findByIdOrElseThrow(userId, NOT_FOUNT_USER);
    }

    private Store getStore(Long storeId) {
        return storeRepo.findByIdOrElseThrow(storeId, NOT_FOUND_STORE);
    }
}
