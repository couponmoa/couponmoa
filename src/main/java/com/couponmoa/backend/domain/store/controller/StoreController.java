package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @RequestBody StoreRequest request,
            @RequestParam Long userId) { //임시로 userId를 파라미터로 받음
        StoreResponse response = storeService.createStore(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "가게가 성공적으로 등록되었습니다"));

    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> getStore(@PathVariable Long storeId) {
        StoreResponse response = storeService.getStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
