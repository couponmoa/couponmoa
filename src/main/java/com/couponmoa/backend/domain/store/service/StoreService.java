package com.couponmoa.backend.domain.store.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.enums.StoreCategory;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoreResponse createStore(StoreRequest request, AuthUser authUser) {
        User user = userRepository.findById(authUser.getId())
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
    public List<StoreResponse> getStoreList(StoreCategory category) {
        List<Store> stores = (category == null)
                ? storeRepository.findAll()
                : storeRepository.findByStoreCategory(category);

        List<StoreResponse> responseList = new ArrayList<>();
        for (Store store : stores) {
            responseList.add(new StoreResponse(
                    store.getId(),
                    store.getName(),
                    store.getDescription(),
                    store.getAddress(),
                    store.getStoreCategory()
            ));
        }
        return responseList;
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

    @Transactional
    public StoreResponse updateStore(Long storeId, StoreRequest request, AuthUser authUser) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException("가게를 찾을 수 없습니다"));
        if (!store.getUser().getId().equals(authUser.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
        }

        store.update(
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

    @Transactional
    public void deleteStore(Long storeId, AuthUser authUser) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException("가게를 찾을 수 없습니다"));
        if (!store.getUser().getId().equals(authUser.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
        }
        storeRepository.delete(store);
    }
}
