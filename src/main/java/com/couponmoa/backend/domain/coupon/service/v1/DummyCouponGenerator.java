package com.couponmoa.backend.domain.coupon.service.v1;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DummyCouponGenerator {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public void generateDummyCoupons(int count) {
        // 1. 유저 먼저 생성
        User user = userRepository.save(new User(
                "dummyuser@couponmoa.com",
                "encoded_password",  // 실제 서비스라면 인코딩 필요
                "더미유저",
                UserRole.ROLE_ADMIN
        ));

        // 2. 유저를 연결한 스토어 생성
        Store store = Store.builder()
                .user(user)
                .name("테스트용 스토어")
                .description("쿠폰 더미용 스토어입니다.")
                .address("서울시 테스트구 테스트동")
                .build();

        storeRepository.save(store);

        // 3. 쿠폰 생성
        List<Coupon> couponList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Coupon coupon = Coupon.builder()
                    .name("할인 쿠폰 " + i)
                    .totalQuantity(1000)
                    .discountAmount(BigDecimal.valueOf(1000))
                    .discountRate(BigDecimal.ZERO)
                    .minOrderAmount(BigDecimal.valueOf(10000))
                    .maxDiscountAmount(BigDecimal.valueOf(5000))
                    .description("테스트용 쿠폰입니다.")
                    .startDate(LocalDateTime.now().minusDays(10))
                    .endDate(LocalDateTime.now().plusDays(10))
                    .expiryDate(LocalDateTime.now().plusDays(30))
                    .store(store)  // 여기서 아까 저장한 store 사용
                    .build();
            couponList.add(coupon);

            if (couponList.size() == 1000) {
                couponRepository.saveAll(couponList);
                couponList.clear();
            }
        }

        if (!couponList.isEmpty()) {
            couponRepository.saveAll(couponList);
        }

        System.out.println(count + "건 더미 쿠폰 생성 완료!");
    }
}
