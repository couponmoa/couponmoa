package com.couponmoa.backend.domain.coupon.service.v1;

import com.couponmoa.backend.domain.coupon.converter.CouponConverter;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.entity.Search;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.coupon.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyElasticsearchSyncService {

    private final CouponRepository couponRepository;
    private final SearchRepository searchRepository;

    public int syncAllCouponsToElasticsearch() {
        List<Coupon> coupons = couponRepository.findAll();
        List<Search> searchList = new ArrayList<>();

        for (Coupon coupon : coupons) {
            searchList.add(CouponConverter.toSearchDocument(coupon));
        }

        searchRepository.saveAll(searchList);
        log.info("Elasticsearch로 {}건 bulk insert 완료!", searchList.size());

        return searchList.size();
    }
}
