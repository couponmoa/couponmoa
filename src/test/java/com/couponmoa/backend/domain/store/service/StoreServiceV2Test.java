package com.couponmoa.backend.domain.store.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.domain.store.dto.request.StoreCursor;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreSimpleResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreQueryDslRepository;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceV2Test {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreQueryDslRepository storeQueryDslRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private StoreServiceV2 storeServiceV2;

    private User user;
    private Store store;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "password", "nickname", UserRole.ROLE_ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);

        store = new Store(user, "테스트 가게", "가게 설명", "가게 주소");
        ReflectionTestUtils.setField(store, "id", 1L);
    }

    @Test
    void 스토어_생성_성공() {
        // Given
        StoreRequest request = new StoreRequest("새로운 가게", "새로운 설명", "새로운 주소");
        when(userRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(user);

        Store newStore = new Store(user, request.getName(), request.getDescription(), request.getAddress());
        ReflectionTestUtils.setField(newStore, "id", 2L);
        when(storeRepository.save(any(Store.class))).thenReturn(newStore);

        // When
        StoreResponse response = storeServiceV2.createStore(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals("새로운 가게", response.getName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void 키워드로_스토어_조회_초기_요청_성공() {
        // Given
        StoreCursor cursor = new StoreCursor(null, null);
        List<StoreResponse> stores = Collections.singletonList(StoreResponse.toDto(store));
        when(storeQueryDslRepository.searchStoresByKeyword(any(), anyInt())).thenReturn(stores);

        // When
        List<StoreResponse> result = storeServiceV2.findStoresByKeyword(cursor, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals("테스트 가게", result.get(0).getName());
        verify(storeQueryDslRepository, times(1)).searchStoresByKeyword(any(), anyInt());
    }

    @Test
    void 키워드로_스토어_조회_커서_있을_때_성공() {
        // Given
        StoreCursor cursor = new StoreCursor("테스트", 1L);
        List<StoreResponse> stores = Collections.singletonList(StoreResponse.toDto(store));
        when(storeQueryDslRepository.searchStoresByKeyword(any(), anyInt())).thenReturn(stores);

        // When
        List<StoreResponse> result = storeServiceV2.findStoresByKeyword(cursor, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals("테스트 가게", result.get(0).getName());
        verify(storeQueryDslRepository, times(1)).searchStoresByKeyword(any(), anyInt());
    }

    @Test
    void 키워드로_스토어_조회_결과_없음() {
        // Given
        StoreCursor cursor = new StoreCursor("존재하지않는_스토어", null);
        when(storeQueryDslRepository.searchStoresByKeyword(any(), anyInt())).thenReturn(Collections.emptyList());

        // When
        List<StoreResponse> result = storeServiceV2.findStoresByKeyword(cursor, 10);

        // Then
        assertTrue(result.isEmpty());
        verify(storeQueryDslRepository, times(1)).searchStoresByKeyword(any(), anyInt());
    }

    @Test
    void 스토어_상세_조회_성공() {
        // Given
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);

        // When
        StoreResponse response = storeServiceV2.findStore(1L);

        // Then
        assertNotNull(response);
        assertEquals("테스트 가게", response.getName());
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 스토어_상세_조회_실패_존재하지_않음() {
        // Given
        when(storeRepository.findByIdOrElseThrow(anyLong(), any()))
                .thenThrow(new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        // When & Then
        assertThrows(ApplicationException.class, () -> storeServiceV2.findStore(1L));
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 내_스토어_목록_조회_성공() {
        // Given
        when(storeRepository.findByUserIdAndDeletedAtIsNull(anyLong())).thenReturn(Collections.singletonList(store));

        // When
        List<StoreResponse> responses = storeServiceV2.findMyStores(1L);

        // Then
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("테스트 가게", responses.get(0).getName());
        verify(storeRepository, times(1)).findByUserIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    void 내_간단한_스토어_목록_조회_성공() {
        // Given
        when(storeRepository.findByUserIdAndDeletedAtIsNull(anyLong())).thenReturn(Collections.singletonList(store));

        // When
        List<StoreSimpleResponse> responses = storeServiceV2.findMySimpleStores(1L);

        // Then
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("테스트 가게", responses.get(0).getName());
        verify(storeRepository, times(1)).findByUserIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    void 스토어_수정_성공() {
        // Given
        StoreRequest request = new StoreRequest("수정된 가게", "수정된 설명", "수정된 주소");
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0)); // getArgument(0) : save()시 전달된 첫번째 파라미터 >> Store객체

        // When
        StoreResponse response = storeServiceV2.updateStore(1L, request, 1L);

        // Then
        assertNotNull(response);
        assertEquals("수정된 가게", response.getName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void 스토어_수정_실패_권한_없음() {
        // Given
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);

        // When & Then
        assertThrows(ApplicationException.class, () -> storeServiceV2.updateStore(1L, new StoreRequest(), 2L));
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 스토어_삭제_성공() {
        // Given
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);

        // When
        storeServiceV2.deleteStore(1L, 1L);

        // Then
        assertNotNull(store.getDeletedAt());
        verify(storeRepository, never()).delete(any()); // 실제 DB에서 삭제되는 건 아님
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 스토어_삭제_실패_권한_없음() {
        // Given
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);

        // When & Then
        assertThrows(ApplicationException.class, () -> storeServiceV2.deleteStore(1L, 2L));
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void fallback_키워드로_스토어_조회_성공() {
        // Given
        when(storeRepository.findAll()).thenReturn(List.of(store));
        StoreCursor cursor = new StoreCursor("test", null);
        int size = 10;

        // When
        List<StoreResponse> result = storeServiceV2.fallbackFindStoresByKeyword(cursor, size, new RuntimeException("Simulated Redis failure"));

        // Then
        assertFalse(result.isEmpty());
        assertEquals("테스트 가게", result.get(0).getName());
        verify(storeRepository, times(1)).findAll();
    }

    @Test
    void fallback_스토어_상세_조회_성공() {
        // Given
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);

        // When
        StoreResponse result = storeServiceV2.fallbackFindStore(1L, new RuntimeException("Simulated Redis failure"));

        // Then
        assertNotNull(result);
        assertEquals("테스트 가게", result.getName());
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }
}