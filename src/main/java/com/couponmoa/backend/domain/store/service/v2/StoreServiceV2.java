package com.couponmoa.backend.domain.store.service.v2;


import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.store.dto.request.StoreCursor;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreSimpleResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreQueryDslRepository;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceV2 {

    private final StoreRepository storeRepository;
    private final StoreQueryDslRepository storeQueryDslRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "stores", allEntries = true)
    public StoreResponse createStore(StoreRequest request, Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId, ErrorCode.USER_NOT_FOUND);

        // 동일한 이름의 스토어가 이미 존재하는지 확인
        validateStoreNameUniqueness(null,request.getName(),false);

        Store store = new Store(
                user,
                request.getName(),
                request.getDescription(),
                request.getAddress()
        );
        Store savedStore = storeRepository.save(store);

        return StoreResponse.toDto(savedStore);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "stores", key = "T(com.couponmoa.backend.common.util.CacheKeyGenerator).generateCacheKey(#cursor, #size)")
    @Retry(name = "storeService", fallbackMethod = "fallbackFindStoresByKeyword")
    public List<StoreResponse> findStoresByKeyword(StoreCursor cursor, int size) {
        return storeQueryDslRepository.searchStoresByKeyword(cursor, size);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "stores", key = "T(com.couponmoa.backend.common.util.CacheKeyGenerator).generateStoreCacheKey(#storeId)")
    @Retry(name = "storeService", fallbackMethod = "fallbackFindStore")
    public StoreResponse findStore(Long storeId) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        return StoreResponse.toDto(store);
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> findMyStores(Long userId) {
        if (userId == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<Store> stores = storeRepository.findByUserIdAndDeletedAtIsNull(userId);
        return stores.stream().map(StoreResponse::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StoreSimpleResponse> findMySimpleStores(Long userId) {
        if (userId == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<Store> stores = storeRepository.findByUserIdAndDeletedAtIsNull(userId);
        return stores.stream()
                .map(StoreSimpleResponse::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "stores", allEntries = true)
    public StoreResponse updateStore(Long storeId, StoreRequest request, Long userId) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        validateStoreOwner(store, userId);

        // 기존 이름과 새 이름을 비교하여 동일한 이름이 아닌 경우에만 중복 체크
        validateStoreNameUniqueness(store.getName(),request.getName(),true);

        store.update(request.getName(), request.getDescription(), request.getAddress());

        storeRepository.save(store);

        return StoreResponse.toDto(store);
    }

    @Transactional
    @CacheEvict(value = "stores", allEntries = true)
    public void deleteStore(Long storeId, Long userId) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        validateStoreOwner(store, userId);

        if (store.getDeletedAt() != null) {
            throw new ApplicationException(ErrorCode.ALREADY_DELETED);
        }

        store.delete();
    }

    private void validateStoreOwner(Store store, Long userId) {
        if (!store.getUser().getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
        }
    }

    private void validateStoreNameUniqueness(String oldStoreName,String newStoreName,boolean isUpdate) {
        // oldStoreName이 있을 때만, 요청의 새 이름과 비교.
        if (oldStoreName != null && oldStoreName.equals(newStoreName)) {
            return;
        }

        // 생성 또는 이름이 변경된 경우에만 중복 체크
        if (!isUpdate && storeRepository.existsByNameAndDeletedAtIsNull(newStoreName)) {
            throw new ApplicationException(ErrorCode.DUPLICATE_RESOURCE);
        }
    }

    // findStoresByKeyword 실패 시 fallback 메서드
    public List<StoreResponse> fallbackFindStoresByKeyword(StoreCursor cursor, int size, Exception e) {

        log.info("Redis 장애 발생, DB에서 조회: " + e.getMessage());

        List<Store> stores = storeRepository.findAll();
        return stores.stream().map(StoreResponse::toDto).collect(Collectors.toList());
    }

    // findStore 실패 시 fallback 메서드
    public StoreResponse fallbackFindStore(Long storeId, Exception e) {

        log.info("Redis 장애 발생, DB에서 조회: " + e.getMessage());

        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        return StoreResponse.toDto(store);
    }
}
