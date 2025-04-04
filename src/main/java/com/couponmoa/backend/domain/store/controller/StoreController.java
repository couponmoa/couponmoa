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
public class StoreController {

    private final StoreService storeService;

    //가게 생성을 ADMIN만 가능하게 수정
    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 되어 있지 않습니다");
        }
        boolean isAdmin = false;
        for (GrantedAuthority authority : authUser.getAuthorities()) {
            if (authority.getAuthority().equals(UserRole.ROLE_ADMIN.name())) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY, "관리자만 가게를 생성할 수 있습니다");
        }
        StoreResponse response = storeService.createStore(request, authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "가게가 성공적으로 등록되었습니다"));

    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getMyStore(
            @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다");
        }
        List<StoreResponse> response = storeService.getMyStore(authUser.getId()); // category 제거
        return ResponseEntity.ok(ApiResponse.success(response, "가게 목록 조회 성공"));
    }

    @GetMapping("/my/simple")
    public ResponseEntity<ApiResponse<List<SimpleStoreResponse>>> getMySimpleStores(
            @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다");
        }
        List<SimpleStoreResponse> response = storeService.getMySimpleStores(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "내 가게 간단 목록 조회 성공"));
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
