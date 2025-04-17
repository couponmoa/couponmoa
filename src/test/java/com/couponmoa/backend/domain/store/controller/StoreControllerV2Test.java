package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.TestSecurityConfig;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreSimpleResponse;
import com.couponmoa.backend.domain.store.service.StoreServiceV2;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreControllerV2.class)
@Import(TestSecurityConfig.class)
class StoreControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreServiceV2 storeServiceV2;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private JwtAuthenticationToken adminAuthenticationToken;

    @BeforeEach
    void setUp() {
        AuthUser adminUser = new AuthUser(1L, "admin@example.com", UserRole.ROLE_ADMIN);
        adminAuthenticationToken = new JwtAuthenticationToken(adminUser);
        SecurityContextHolder.getContext().setAuthentication(adminAuthenticationToken);
    }

    @Test
    void 스토어_생성_성공() throws Exception {
        // Given
        StoreRequest request = new StoreRequest("새로운 가게", "가게 설명", "가게 주소");
        StoreResponse response = new StoreResponse(1L, "새로운 가게", "가게 설명", "가게 주소");
        when(storeServiceV2.createStore(any(StoreRequest.class), anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("새로운 가게"));
    }

    @Test
    void 키워드로_스토어_조회_성공() throws Exception {
        // Given
        StoreResponse response = new StoreResponse(1L, "테스트 가게", "가게 설명", "가게 주소");
        when(storeServiceV2.findStoresByKeyword(any(), anyInt())).thenReturn(Collections.singletonList(response));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/stores")
                        .param("keyword", "테스트")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("테스트 가게"));
    }

    @Test
    void 키워드로_스토어_조회_결과없음() throws Exception {
        // Given
        when(storeServiceV2.findStoresByKeyword(any(), anyInt())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/stores")
                        .param("keyword", "존재하지않는가게")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 내_스토어_목록_조회_성공() throws Exception {
        // Given
        StoreResponse response = new StoreResponse(1L, "나의 가게", "가게 설명", "가게 주소");
        when(storeServiceV2.findMyStores(anyLong())).thenReturn(Collections.singletonList(response));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/stores/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("나의 가게"));
    }

    @Test
    void 내_간단한_스토어_목록_조회_성공() throws Exception {
        // Given
        StoreSimpleResponse response = new StoreSimpleResponse(1L, "간단한 가게");
        when(storeServiceV2.findMySimpleStores(anyLong())).thenReturn(Collections.singletonList(response));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/stores/my/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("간단한 가게"));
    }

    @Test
    void 스토어_상세_조회_성공() throws Exception {
        // Given
        StoreResponse response = new StoreResponse(1L, "상세 가게", "가게 설명", "가게 주소");
        when(storeServiceV2.findStore(anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/stores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("상세 가게"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 스토어_수정_성공() throws Exception {
        // Given
        StoreRequest request = new StoreRequest("수정된 가게", "수정된 설명", "수정된 주소");
        StoreResponse response = new StoreResponse(1L, "수정된 가게", "수정된 설명", "수정된 주소");
        when(storeServiceV2.updateStore(anyLong(), any(StoreRequest.class), anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v2/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 가게"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 스토어_삭제_성공() throws Exception {
        // Given
        doNothing().when(storeServiceV2).deleteStore(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v2/stores/1"))
                .andExpect(status().isNoContent());
    }
}