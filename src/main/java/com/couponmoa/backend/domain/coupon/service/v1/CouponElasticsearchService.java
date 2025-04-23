package com.couponmoa.backend.domain.coupon.service.v1;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.converter.CouponConverter;
import com.couponmoa.backend.domain.coupon.entity.Search;
import com.couponmoa.backend.domain.coupon.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponElasticsearchService {

    private final SearchRepository searchRepository;

    public void save(Coupon coupon) {
        Search searchDocument = CouponConverter.toSearchDocument(coupon);
        searchRepository.save(searchDocument);
    }

    public void deleteCouponFromElasticsearch(Long couponId) {
        searchRepository.deleteById(couponId);
    }

    public List<Search> recommendCoupons(String keyword) {
        List<Search> coupons = searchRepository.findByNameContaining(keyword);
        log.info("Elasticsearch 검색 결과 {}건", coupons.size());
        return coupons;
    }
}