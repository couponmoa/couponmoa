package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.SimpleStoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.service.StoreService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@Tag(name = "Store API", description = "가게 관리 API")
public class StoreController {

    private final StoreService storeService;

    //가게 생성을 ADMIN만 가능하게 수정
    @PostMapping
    @Operation(summary = "가게 생성",
               description = "관리자 권한을 가진 사용자가 새로운 가게를 생성합니다." )
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        StoreResponse response = storeService.createStore(request, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "가게가 성공적으로 등록되었습니다"));

    }

    @GetMapping("/my")
    @Operation(summary = "내 가게 목록 조회",
               description = "로그인한 사용자가 자신이 생성한 가게 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getMyStore(
            @AuthenticationPrincipal AuthUser authUser) {
        List<StoreResponse> response = storeService.getMyStore(authUser.getId()); // category 제거
        return ResponseEntity.ok(ApiResponse.success(response, "가게 목록 조회 성공"));
    }

    @GetMapping("/my/simple")
    @Operation(summary = "내 가게 간단 목록 조회",
               description = "로그인 한 사용자가 자신의 가게 ID와 이름만 포함한 간단 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<SimpleStoreResponse>>> getMySimpleStores(
            @AuthenticationPrincipal AuthUser authUser) {
        List<SimpleStoreResponse> response = storeService.getMySimpleStores(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "내 가게 간단 목록 조회 성공"));
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "특정 가게 조회",
               description = "가게 ID를 통해 특정 가게의 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<StoreResponse>> getStore(@PathVariable Long storeId) {
        StoreResponse response = storeService.getStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{storeId}")
    @Operation(summary = "가게 정보 수정",
               description = "특정 가게의 정보를 수정합니다")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long storeId,
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        StoreResponse response = storeService.updateStore(storeId, request, authUser);
        return ResponseEntity.ok(ApiResponse.success(response, "가게 정보가 수정되었습니다"));
    }

    @DeleteMapping("/{storeId}")
    @Operation(summary = "가게 삭제",
               description = "특정 가게를 삭제합니다")
    public ResponseEntity<ApiResponse<StoreResponse>> deleteStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal AuthUser authUser) {
        storeService.deleteStore(storeId, authUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
