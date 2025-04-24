package com.couponmoa.backend.domain.coupon.service.v1;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.entity.Search;
import com.couponmoa.backend.domain.coupon.repository.SearchRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponElasticsearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private CouponElasticsearchService couponElasticsearchService;

    @Test
    @DisplayName("Elasticsearch 저장 테스트")
    void saveCouponToElasticsearch() {
        // given
        User user = new User();
        user.setId(1L);

        Store store = new Store();
        store.setId(100L);
        store.setUser(user);
        store.setName("테스트 상점");
        store.setDescription("테스트 설명");
        store.setAddress("서울시 어딘가");

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setName("테스트 쿠폰");
        coupon.setTotalQuantity(100);
        coupon.setIssuedQuantity(0);
        coupon.setDiscountAmount(new BigDecimal("1000.00"));
        coupon.setDiscountRate(new BigDecimal("10.0"));
        coupon.setMinOrderAmount(new BigDecimal("5000.00"));
        coupon.setMaxDiscountAmount(new BigDecimal("5000.00"));
        coupon.setDescription("쿠폰 설명");
        coupon.setStartDate(LocalDateTime.now().minusDays(1));
        coupon.setEndDate(LocalDateTime.now().plusDays(5));
        coupon.setExpiryDate(LocalDateTime.now().plusDays(10));
        coupon.setStore(store);

        Search search = new Search();
        search.setCouponId(1L);
        search.setName("테스트 쿠폰");

        when(searchRepository.save(any(Search.class))).thenReturn(search);

        // when
        couponElasticsearchService.save(coupon);

        // then
        verify(searchRepository, times(1)).save(any(Search.class));
    }

    @Test
    @DisplayName("Elasticsearch 삭제 테스트")
    void deleteCouponFromElasticsearch() {
        // given
        Long couponId = 1L;

        // when
        couponElasticsearchService.deleteCouponFromElasticsearch(couponId);

        // then
        verify(searchRepository, times(1)).deleteById(couponId);
    }

    @Test
    @DisplayName("Elasticsearch 쿠폰 추천 검색 테스트")
    void recommendCoupons() {
        // given
        String keyword = "할인";
        Search search = new Search();
        search.setCouponId(1L);
        search.setName("할인 쿠폰");

        when(searchRepository.findByNameContaining(keyword)).thenReturn(Collections.singletonList(search));

        // when
        List<Search> result = couponElasticsearchService.recommendCoupons(keyword);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).contains(keyword);
        verify(searchRepository, times(1)).findByNameContaining(keyword);
    }
}
