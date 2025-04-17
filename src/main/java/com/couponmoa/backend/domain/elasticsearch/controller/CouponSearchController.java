package com.couponmoa.backend.domain.elasticsearch.controller;

import com.couponmoa.backend.domain.elasticsearch.entity.Search;
import com.couponmoa.backend.domain.elasticsearch.service.CouponSearchService;
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

    private final CouponSearchService couponSearchService;

    //이름으로 쿠폰 검색
    @GetMapping
    public List<Search> search(@RequestParam(name = "keyword") String keyword) {
        return couponSearchService.searchCouponsByName(keyword);
    }

}

