package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.enums.StoreCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    // 단일 스토어 조회 (Mock 데이터)
    @GetMapping("/{storeId}")
    public ResponseEntity<Store> getStore(@PathVariable Long storeId) {
        Store mockStore = new Store(
                "테스트 상점",
                "이곳은 테스트 상점입니다.",
                "서울 강남구 테헤란로 123",
                StoreCategory.한식 // 예제용으로 FOOD 카테고리 사용
        );
        return ResponseEntity.ok(mockStore);
    }

    // 전체 스토어 리스트 조회 (Mock 데이터)
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> mockStores = new ArrayList<>();
        mockStores.add(new Store("카페 모카", "맛있는 커피를 제공합니다.", "서울 강남구", StoreCategory.한식));
        mockStores.add(new Store("한식당 서울", "정통 한식을 맛볼 수 있습니다.", "서울 종로구", StoreCategory.한식));
        mockStores.add(new Store("편의점 24시", "24시간 영업하는 편의점입니다.", "서울 마포구", StoreCategory.한식));

        return ResponseEntity.ok(mockStores);
    }
}