package com.couponmoa.backend.domain.elasticsearch.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.elasticsearch.converter.CouponConverter;
import com.couponmoa.backend.domain.elasticsearch.entity.Search;
import com.couponmoa.backend.domain.elasticsearch.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponElasticsearchService {

    private final SearchRepository searchRepository;

    // 쿠폰을 Elasticsearch에 저장
    public void save(Coupon coupon) {
        // 쿠폰을 Elasticsearch 문서 형식으로 변환
        Search searchDocument = CouponConverter.toSearchDocument(coupon);

        // Elasticsearch에 저장
        searchRepository.save(searchDocument);
    }

    public void deleteCouponFromElasticsearch(Long couponId) {
        searchRepository.deleteById(couponId);
    }

}
