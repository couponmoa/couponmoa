package com.couponmoa.backend.domain.coupon.controller.v1;

import com.couponmoa.backend.domain.coupon.entity.Search;
import com.couponmoa.backend.domain.coupon.service.v1.CouponElasticsearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CouponSearchControllerTest {

    @Mock
    private CouponElasticsearchService couponElasticsearchService;

    @InjectMocks
    private CouponSearchController couponSearchController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponSearchController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void searchCoupons_withValidKeyword_returnsCouponList() throws Exception {
        // 준비
        String keyword = "coffee";
        Search coupon1 = Search.builder()
                .couponId(1L)
                .name("Coffee Coupon")
                .description("커피 10% 할인")
                .discountAmount(new BigDecimal("2.00"))
                .discountRate(new BigDecimal("10.00"))
                .minOrderAmount(new BigDecimal("10.00"))
                .maxDiscountAmount(new BigDecimal("5.00"))
                .expiryDate("2025-12-31")
                .storeId(1L)
                .storeName("카페 모아")
                .build();

        Search coupon2 = Search.builder()
                .couponId(2L)
                .name("Coffee Discount")
                .description("구매 시 무료 커피")
                .discountAmount(new BigDecimal("3.00"))
                .discountRate(new BigDecimal("15.00"))
                .minOrderAmount(new BigDecimal("15.00"))
                .maxDiscountAmount(new BigDecimal("7.00"))
                .expiryDate("2025-11-30")
                .storeId(2L)
                .storeName("브루 헤이븐")
                .build();

        List<Search> couponList = Arrays.asList(coupon1, coupon2);

        when(couponElasticsearchService.recommendCoupons(anyString())).thenReturn(couponList);

        // 실행 & 검증
        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Coffee Coupon"))
                .andExpect(jsonPath("$[0].description").value("커피 10% 할인"))
                .andExpect(jsonPath("$[0].discountAmount").value(2.00))
                .andExpect(jsonPath("$[0].discountRate").value(10.00))
                .andExpect(jsonPath("$[0].minOrderAmount").value(10.00))
                .andExpect(jsonPath("$[0].maxDiscountAmount").value(5.00))
                .andExpect(jsonPath("$[0].expiryDate").value("2025-12-31"))
                .andExpect(jsonPath("$[0].storeId").value(1L))
                .andExpect(jsonPath("$[0].storeName").value("카페 모아"))
                .andExpect(jsonPath("$[1].couponId").value(2L))
                .andExpect(jsonPath("$[1].name").value("Coffee Discount"));
    }

    @Test
    void searchCoupons_withEmptyKeyword_returnsEmptyList() throws Exception {
        // 준비
        String keyword = "";
        when(couponElasticsearchService.recommendCoupons(anyString())).thenReturn(Arrays.asList());

        // 실행 & 검증
        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}