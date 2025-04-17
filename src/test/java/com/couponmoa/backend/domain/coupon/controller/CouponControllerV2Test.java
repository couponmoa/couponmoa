package com.couponmoa.backend.domain.coupon.controller;

import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.TestSecurityConfig;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.service.CouponReadServiceV2;
import com.couponmoa.backend.domain.coupon.service.CouponServiceV2;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponControllerV2.class)
@Import(TestSecurityConfig.class)
class CouponControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponServiceV2 couponServiceV2;

    @MockitoBean
    private CouponReadServiceV2 couponReadServiceV2;

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
    @WithMockUser(roles = "ADMIN")
    void 쿠폰_생성_성공() throws Exception {
        // Given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("테스트 쿠폰")
                .totalQuantity(1000)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.ZERO)
                .storeId(1L)
                .build();

        CouponResponse response = new CouponResponse(1L);
        when(couponServiceV2.createCoupon(any(CouponCreateRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void 키워드로_쿠폰_조회_성공() throws Exception {
        // Given
        List<CouponSimpleResponse> coupons = Collections.singletonList(
                CouponSimpleResponse.builder().id(1L).name("테스트").discountAmount(BigDecimal.TEN).discountRate(BigDecimal.ONE).startDate(LocalDateTime.now()).endDate(LocalDateTime.now()).status(CouponStatus.IN_PROGRESS).build()
        );
        when(couponReadServiceV2.findCouponsByKeyword(any(), any(), anyInt())).thenReturn(coupons);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/coupons")
                        .param("keyword", "테스트")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("테스트"));
    }

    @Test
    void 스토어별_쿠폰_조회_성공() throws Exception {
        // Given
        Page<CouponSimpleResponse> coupons = new PageImpl<>(Collections.singletonList(
                CouponSimpleResponse.builder().id(1L).name("스토어 쿠폰").discountAmount(BigDecimal.TEN).discountRate(BigDecimal.ONE).startDate(LocalDateTime.now()).endDate(LocalDateTime.now()).status(CouponStatus.IN_PROGRESS).build()
        ));
        when(couponReadServiceV2.findCouponsByStore(anyLong(), any(CouponSearchByStoreRequest.class), anyInt(), anyInt())).thenReturn(coupons);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/coupons/store/1")
                        .param("size", "10")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("스토어 쿠폰"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 쿠폰_수정_성공() throws Exception {
        // Given
        CouponUpdateRequest request = CouponUpdateRequest.builder()
                .name("수정된 쿠폰")
                .newTotalQuantity(500)
                .endDate(LocalDateTime.of(2025, 6, 1, 0, 0))
                .storeId(1L)
                .build();

        CouponResponse response = new CouponResponse(1L);
        when(couponServiceV2.updateCoupon(anyLong(), any(CouponUpdateRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v2/coupons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 쿠폰_삭제_성공() throws Exception {
        // Given
        doNothing().when(couponServiceV2).deleteCoupon(anyLong());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v2/coupons/1"))
                .andExpect(status().isNoContent());
    }
}