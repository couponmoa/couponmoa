package com.couponmoa.backend.domain.store.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.Store;
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
        User user = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.USER_NOT_FOUND);
        Store store = new Store(
                user,
                request.getName(),
                request.getDescription(),
                request.getAddress()
        );
        Store savedStore = storeRepository.save(store);
        return new StoreResponse(
                savedStore.getId(),
                savedStore.getName(),
                savedStore.getDescription(),
                savedStore.getAddress()
        );
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> getStoreList() {
        List<Store> stores = storeRepository.findAll();
        List<StoreResponse> responses = new ArrayList<>();
        for (Store store : stores) {
            responses.add(new StoreResponse(
                    store.getId(),
                    store.getName(),
                    store.getDescription(),
                    store.getAddress()
            ));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getAddress()
        );
    }

    @Transactional
    public StoreResponse updateStore(Long storeId, StoreRequest request, AuthUser authUser) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        if (!store.getUser().getId().equals(authUser.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
        }

        store.update(
                request.getName(),
                request.getDescription(),
                request.getAddress()
        );
        Store savedStore = storeRepository.save(store);
        return new StoreResponse(
                savedStore.getId(),
                savedStore.getName(),
                savedStore.getDescription(),
                savedStore.getAddress()
        );
    }

    @Transactional
    public void deleteStore(Long storeId, AuthUser authUser) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        if (!store.getUser().getId().equals(authUser.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
        }
        storeRepository.delete(store);
    }
}
