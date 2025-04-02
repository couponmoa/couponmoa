package com.couponmoa.backend.domain.store.service;

import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoreResponse createStore(StoreRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다"));
        Store store = new Store(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                request.getStoreCategory()
        );
        Store savedStore = storeRepository.save(store);
        return new StoreResponse(
                savedStore.getId(),
                savedStore.getName(),
                savedStore.getDescription(),
                savedStore.getAddress(),
                savedStore.getStoreCategory()
        );
    }

    @Transactional(readOnly = true)
    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("가게를 찾을 수 없습니다"));
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getAddress(),
                store.getStoreCategory()
        );
    }
}
