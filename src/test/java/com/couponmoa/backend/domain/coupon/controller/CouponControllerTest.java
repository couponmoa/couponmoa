package com.couponmoa.backend.domain.coupon.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.SecurityConfig;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.service.CouponReadService;
import com.couponmoa.backend.domain.coupon.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@Import(SecurityConfig.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private CouponReadService couponReadService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void 쿠폰_생성_성공() throws Exception {
        // Given
        CouponCreateRequestDto requestDto = new CouponCreateRequestDto(
                "테스트용_할인_쿠폰", 100, BigDecimal.valueOf(1000), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.valueOf(10000), "테스트 쿠폰입니다.",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                1L
        );

        CouponResponseDto responseDto = new CouponResponseDto(1L);

        given(couponService.createCoupon(any(CouponCreateRequestDto.class)))
                .willReturn(responseDto);

        // When
        ResultActions result = mockMvc.perform(post("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @WithMockUser
    void 쿠폰_목록_조회_성공() throws Exception {
        // Given
        CouponSimpleResponseDto dto1 = CouponSimpleResponseDto.builder()
                .id(1L)
                .name("쿠폰A")
                .availableQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.ZERO)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        CouponSimpleResponseDto dto2 = CouponSimpleResponseDto.builder()
                .id(2L)
                .name("쿠폰B")
                .availableQuantity(50)
                .discountAmount(BigDecimal.valueOf(2000))
                .discountRate(BigDecimal.ZERO)
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        Page<CouponSimpleResponseDto> page = new PageImpl<>(List.of(dto1, dto2));

        given(couponReadService.findAllCoupons(anyInt(), anyInt()))
                .willReturn(page);

        // When
        ResultActions result = mockMvc.perform(get("/api/v1/coupons")
                .param("page", "1")
                .param("size", "10"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("쿠폰A"))
                .andExpect(jsonPath("$.data.content[1].availableQuantity").value(50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 쿠폰_상세_조회_성공() throws Exception {
        // Given
        Coupon coupon = Coupon.builder()
                .name("테스트용_쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.ZERO)
                .minOrderAmount(BigDecimal.valueOf(5000))
                .maxDiscountAmount(null)
                .description("테스트를_위한_쿠폰입니다.")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .expiryDate(LocalDateTime.now().plusDays(10))
                .status(CouponStatus.IN_PROGRESS)
                .build();

        ReflectionTestUtils.setField(coupon, "id", 1L);

        CouponDetailResponseDto responseDto = CouponDetailResponseDto.toDto(coupon);

        given(couponReadService.findCoupon(eq(1L), any()))
                .willReturn(responseDto);

        // When
        ResultActions result = mockMvc.perform(get("/api/v1/coupons/1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("테스트용_쿠폰"))
                .andExpect(jsonPath("$.data.totalQuantity").value(100))
                .andExpect(jsonPath("$.data.discountAmount").value(1000))
                .andExpect(jsonPath("$.data.minOrderAmount").value(5000))
                .andExpect(jsonPath("$.data.description").value("테스트를_위한_쿠폰입니다."))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }
}