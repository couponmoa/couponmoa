package com.couponmoa.backend.domain.coupon.controller.v1;

import com.couponmoa.backend.domain.coupon.service.v1.DummyElasticsearchSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DummyElasticsearchSyncController {

    private final DummyElasticsearchSyncService dummyElasticsearchSyncService;

    @PostMapping("/sync-coupons-to-elasticsearch")
    public String syncCoupons() {
        int count = dummyElasticsearchSyncService.syncAllCouponsToElasticsearch();
        return count + "건 Elasticsearch로 Bulk Insert 완료!";
    }
}
