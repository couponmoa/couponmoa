package com.couponmoa.backend.domain.coupon.controller.v1;

import com.couponmoa.backend.domain.coupon.entity.Search;
import com.couponmoa.backend.domain.coupon.service.v1.CouponElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class CouponSearchController {

    private final CouponElasticsearchService couponElasticsearchService;

    // 이름 검색
    @GetMapping
    public List<Search> search(@RequestParam(name = "keyword") String keyword) {
        return couponElasticsearchService.recommendCoupons(keyword);
    }
}

