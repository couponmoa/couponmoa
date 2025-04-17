package com.couponmoa.backend.domain.store.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.SimpleStoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreService storeService;

    private User user;
    private AuthUser authUser;
    private Store store;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        user = new User("test@example.com", "password", "nickname", UserRole.ROLE_USER);
        user.setId(1L);
        authUser = new AuthUser(1L, "test@example.com", UserRole.ROLE_USER);
        store = new Store(user, "Test Store", "Description", "Address");

        Field idField = Store.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(store, 1L);
    }

    @Test
    void 관리자일_경우_가게_생성_성공() {
        AuthUser adminUser = new AuthUser(1L, "admin@example.com", UserRole.ROLE_ADMIN);
        StoreRequest request = new StoreRequest("Test Store", "Description", "Address");
        when(userRepository.findByIdOrElseThrow(1L, ErrorCode.USER_NOT_FOUND)).thenReturn(user);
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        StoreResponse response = storeService.createStore(request, adminUser);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Store", response.getName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void 관리자가_아니면_가게_생성_실패() {
        StoreRequest request = new StoreRequest("New Store", "New Desc", "New Address");

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> storeService.createStore(request, authUser));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("관리자만 가게를 생성할 수 있습니다", exception.getMessage());
        verify(storeRepository, never()).save(any());
    }

    @Test
    void 인증되지_않은_사용자는_가게_생성_실패() {
        StoreRequest request = new StoreRequest("New Store", "New Desc", "New Address");

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> storeService.createStore(request, null));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("로그인이 되어 있지 않습니다", exception.getMessage());
        verify(storeRepository, never()).save(any());
    }

    @Test
    void 내_가게_조회_성공() {
        when(storeRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(List.of(store));

        List<StoreResponse> responses = storeService.findMyStore(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Test Store", responses.get(0).getName());
    }

    @Test
    void 내_가게_조회_실패_유저정보_없음() {
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> storeService.findMyStore(null));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("로그인이 필요합니다", exception.getMessage());
    }

    @Test
    void 내_간단한_가게_리스트_조회_성공() {
        when(storeRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(List.of(store));

        List<SimpleStoreResponse> responses = storeService.findMySimpleStores(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Test Store", responses.get(0).getName());
    }

    @Test
    void 내_간단한_가게_리스트_조회_실패_유저정보_없음() {
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> storeService.findMySimpleStores(null));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("로그인이 필요합니다", exception.getMessage());
    }

    @Test
    void 특정_가게_조회_성공() {
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);

        StoreResponse response = storeService.findStore(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Store", response.getName());
    }

    @Test
    void 특정_가게_조회_실패_가게없음() {
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND))
                .thenThrow(new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> storeService.findStore(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("존재하지 않는 스토어 입니다.", exception.getMessage());
    }

    @Test
    void 가게_수정_성공() {
        StoreRequest request = new StoreRequest("Updated Store", "Updated Desc", "Updated Address");
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StoreResponse response = storeService.updateStore(1L, request, authUser);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated Store", response.getName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }


    @Test
    void 가게_삭제_성공() {
        System.out.println("JDK Version: " + System.getProperty("java.version"));
        System.out.println("Test running in: " + System.getProperty("java.class.path"));
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);

        storeService.deleteStore(1L, authUser);

        verify(storeRepository, times(1)).delete(store);
    }

    @Test
    void 가게_삭제_실패_권한없음() {
        AuthUser otherUser = new AuthUser(2L, "other@example.com", UserRole.ROLE_USER);
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> storeService.deleteStore(1L, otherUser));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("ADMIN 권한을 가진 유저만 접근할 수 있습니다.", exception.getMessage());
    }
}
