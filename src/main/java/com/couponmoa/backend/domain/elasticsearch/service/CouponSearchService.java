package com.couponmoa.backend.domain.elasticsearch.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.elasticsearch.converter.CouponConverter;
import com.couponmoa.backend.domain.elasticsearch.entity.Search;
import com.couponmoa.backend.domain.elasticsearch.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CouponSearchService {

    private final SearchRepository searchRepository;

    //쿠폰 등록시 ES 저장
    public void saveCouponToElasticsearch(Coupon coupon) {
        Search document = CouponConverter.toSearchDocument(coupon);
        searchRepository.save(document);
    }

    //쿠폰 검색
    public List<Search> searchCouponsByName(String keyword) {
        return searchRepository.findByNameContaining(keyword);
    }

    // ES 삭제용
    public void deleteCouponFromElasticsearch(Long couponId) {
        searchRepository.deleteById(couponId);
    }
}