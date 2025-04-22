package com.couponmoa.backend.domain.coupon.controller.v1;

import com.couponmoa.backend.domain.coupon.service.v1.DummyCouponGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DummyDataController {

    private final DummyCouponGenerator dummyCouponGenerator;

    @PostMapping("/generate-coupons")
    public String generateCoupons(@RequestParam(defaultValue = "100000") int count) {
        dummyCouponGenerator.generateDummyCoupons(count);
        return count + "건 쿠폰 데이터 생성 완료!";
    }
}
