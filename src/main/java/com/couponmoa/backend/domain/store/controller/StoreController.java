package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.service.StoreService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        StoreResponse response = storeService.createStore(request, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "가게가 성공적으로 등록되었습니다"));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getStoreList() {
        List<StoreResponse> response = storeService.getStoreList(); // category 제거
        return ResponseEntity.ok(ApiResponse.success(response, "가게 목록 조회 성공"));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> getStore(@PathVariable Long storeId) {
        StoreResponse response = storeService.getStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long storeId,
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        StoreResponse response = storeService.updateStore(storeId, request, authUser);
        return ResponseEntity.ok(ApiResponse.success(response, "가게 정보가 수정되었습니다"));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> deleteStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal AuthUser authUser) {
        storeService.deleteStore(storeId, authUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
