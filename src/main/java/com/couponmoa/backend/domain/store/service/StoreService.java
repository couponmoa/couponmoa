package com.couponmoa.backend.domain.store.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreSimpleResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreQueryDslRepository;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreQueryDslRepository storeQueryDslRepository;
    private final UserRepository userRepository;;

    @Transactional
    public StoreResponse createStore(StoreRequest request, AuthUser authUser) {

        if (authUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 되어 있지 않습니다");
        }
        boolean isAdmin = false;
        for (GrantedAuthority authority : authUser.getAuthorities()) {
            if (authority.getAuthority().equals(UserRole.ROLE_ADMIN.name())) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY, "관리자만 가게를 생성할 수 있습니다");
        }

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
    public List<StoreResponse> getMyStore(Long userId) {
        if (userId == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다");
        }
        List<Store> stores = storeRepository.findByUserIdAndDeletedAtIsNull(userId);
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
    public List<StoreSimpleResponse> getMySimpleStores(Long userId) {
        if (userId == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다");
        }
        List<Store> stores = storeRepository.findByUserIdAndDeletedAtIsNull(userId);
        List<StoreSimpleResponse> responses = new ArrayList<>();
        for (Store store : stores) {
            responses.add(new StoreSimpleResponse(
                    store.getId(),
                    store.getName()));
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

    @Transactional(readOnly = true)
    public List<Store> findStoresByName(String storeName) {
        return storeQueryDslRepository.findAllStoreByName(storeName);
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
        // 이미 삭제된 경우 예외 처리
        if (store.getDeletedAt() != null) {
            throw new ApplicationException(ErrorCode.ALREADY_DELETED);
        }
        store.delete();
    }
}
