package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtAuthenticationFilter;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.SecurityConfig;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.SimpleStoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.service.StoreService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreController.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private RedisService redisService;

   /* @MockitoBean
    private JwtUtil jwtUtil;*/

    private JwtAuthenticationToken userAuthToken;
    private JwtAuthenticationToken adminAuthToken;

    private AuthUser userUser;
    private AuthUser adminUser;

    @BeforeEach
    void setup() {
        adminUser = new AuthUser(2L, "admin@test.com", UserRole.ROLE_ADMIN);
        adminAuthToken = new JwtAuthenticationToken(adminUser);
        SecurityContextHolder.getContext().setAuthentication(adminAuthToken);

        userUser = new AuthUser(1L, "user@test.com", UserRole.ROLE_USER);
        userAuthToken = new JwtAuthenticationToken(userUser);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 가게_생성() throws Exception {
        StoreRequest request = new StoreRequest("가게 이름", "설명", "주소");
        StoreResponse response = new StoreResponse(1L, "가게 이름", "설명", "주소");

        given(storeService.createStore(any(StoreRequest.class), any(AuthUser.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/stores")
                        .with(authentication(userAuthToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("가게 이름"))
                .andExpect(jsonPath("$.data.address").value("주소"));
    }

    @Test
    void 내_가게_목록_조회() throws Exception {
        StoreResponse response = new StoreResponse(1L, "가게명", "설명", "주소");
        given(storeService.getMyStore(anyLong())).willReturn(List.of(response));

        mockMvc.perform(get("/api/v1/stores/my")
                        .with(authentication(userAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("가게명"));
    }

    @Test
    void 내_가게_간단_목록_조회() throws Exception {
        SimpleStoreResponse response = new SimpleStoreResponse(1L, "가게명");
        given(storeService.getMySimpleStores(anyLong())).willReturn(List.of(response));

        mockMvc.perform(get("/api/v1/stores/my/simple")
                        .with(authentication(userAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("가게명"));
    }

    @Test
    void 특정_가게_조회() throws Exception {
        Long storeId = 1L;
        StoreResponse response = new StoreResponse(storeId, "가게 이름", "설명", "주소");
        given(storeService.getStore(eq(storeId))).willReturn(response);

        mockMvc.perform(get("/api/v1/stores/{storeId}", storeId)
                        .with(authentication(userAuthToken))
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(storeId))
                .andExpect(jsonPath("$.data.name").value("가게 이름"))
                .andExpect(jsonPath("$.data.description").value("설명"))
                .andExpect(jsonPath("$.data.address").value("주소"));
    }

    @Test
    void 가게_수정() throws Exception {
        Long storeId = 1L;
        StoreRequest request = new StoreRequest("수정된 이름", "수정된 설명", "수정된 주소");
        StoreResponse response = new StoreResponse(storeId, "수정된 이름", "수정된 설명", "수정된 주소");

        given(storeService.updateStore(eq(storeId), any(StoreRequest.class), eq(adminUser)))
                .willReturn(response);

        mockMvc.perform(put("/api/v1/stores/{storeId}", storeId)
                        .with(authentication(adminAuthToken))
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 이름"));
    }

    @Test
    void 가게_삭제() throws Exception {
        Long storeId = 1L;
        willDoNothing().given(storeService).deleteStore(eq(storeId), any(AuthUser.class)); // 수정된 부분

        mockMvc.perform(delete("/api/v1/stores/{storeId}", storeId)
                        .with(authentication(adminAuthToken))
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent())
                .andDo(print());  // 디버깅용
    }
}