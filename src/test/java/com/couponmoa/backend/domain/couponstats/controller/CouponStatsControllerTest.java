package com.couponmoa.backend.domain.couponstats.controller;

import com.couponmoa.backend.config.JwtAuthenticationFilter;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.config.TestSecurityConfig;
import com.couponmoa.backend.domain.couponstats.dto.response.CouponUsageResponse;
import com.couponmoa.backend.domain.couponstats.service.CouponStatsService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@WebMvcTest(
        value = CouponStatsController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)}
)
@Import(TestSecurityConfig.class)
class CouponStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponStatsService couponStatsService;

    private final static String URL_PREFIX = "/api/v1";

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindCouponDailyUsageStatsTests {

        private final static long couponId = 1L;
        private final static String REQUEST_URL = URL_PREFIX + "/coupons/{couponId}/usage-stats";

        @Test
        @Order(1)
        void 쿠폰_일별_사용량_조회_어드민_권한_아님_실패() throws Exception {
            AuthUser authUser = new AuthUser(1L, "temp@gmail.com", UserRole.ROLE_USER);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

            mockMvc.perform(get(REQUEST_URL, couponId)
                            .with(authentication(authentication)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()));
        }

        @Test
        @Order(2)
        void 쿠폰_일별_사용량_조회_어제_이후_날짜_요청_실패() throws Exception {
            AuthUser authUser = new AuthUser(1L, "temp@gmail.com", UserRole.ROLE_USER);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

            mockMvc.perform(get(REQUEST_URL, couponId)
                            .param("end", LocalDate.now().toString())
                            .with(authentication(authentication)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
        }

        @Test
        @Order(3)
        void 쿠폰_일별_사용량_조회_시작_날짜가_종료_날짜_이후임_실패() throws Exception {
            AuthUser authUser = new AuthUser(1L, "temp@gmail.com", UserRole.ROLE_USER);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

            mockMvc.perform(get(REQUEST_URL, couponId)
                            .param("start", LocalDate.now().plusDays(1).toString())
                            .param("end", LocalDate.now().minusDays(1).toString())
                            .with(authentication(authentication)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
        }

        @Test
        @Order(4)
        void 쿠폰_일별_사용량_조회_날짜_범위_30일_초과_실패() throws Exception {
            AuthUser authUser = new AuthUser(1L, "temp@gmail.com", UserRole.ROLE_USER);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

            mockMvc.perform(get(REQUEST_URL, couponId)
                            .param("start", LocalDate.now().minusDays(60).toString())
                            .param("end", LocalDate.now().minusDays(1).toString())
                            .with(authentication(authentication)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
        }

        @Test
        @Order(5)
        void 쿠폰_일별_사용량_조회_성공() throws Exception {
            AuthUser authUser = new AuthUser(1L, "temp@gmail.com", UserRole.ROLE_ADMIN);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

            List<CouponUsageResponse> responseList = List.of(mock(CouponUsageResponse.class));
            given(couponStatsService.findCouponDailyUsageStats(anyLong(), anyLong(), any()))
                    .willReturn(responseList);

            mockMvc.perform(get(REQUEST_URL, couponId)
                            .param("start", LocalDate.now().minusDays(20).toString())
                            .param("end", LocalDate.now().minusDays(1).toString())
                            .with(authentication(authentication)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()));
        }
    }

}