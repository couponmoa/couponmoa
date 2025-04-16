package com.couponmoa.backend.domain.store.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.store.dto.request.StoreCursor;
import com.couponmoa.backend.domain.store.dto.request.StoreRequest;
import com.couponmoa.backend.domain.store.dto.response.StoreResponse;
import com.couponmoa.backend.domain.store.dto.response.StoreSimpleResponse;
import com.couponmoa.backend.domain.store.service.StoreServiceV2;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "스토어 API V2", description = "스토어 관련 기능을 제공하는 API (커서 기반 조회) + 캐싱")
@RestController
@RequestMapping("/api/v2/stores")
@RequiredArgsConstructor
public class StoreControllerV2 {

    private final StoreServiceV2 storeServiceV2;

    @Operation(summary = "스토어 생성", description = "사용자가 새로운 스토어를 생성함")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {

        StoreResponse response = storeServiceV2.createStore(request, authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "키워드로 스토어 조회 (커서 방식)", description = "스토어 이름 기준 키워드로 검색하며, 커서 방식으로 페이징 처리")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreResponse>>> findStoresByKeyword(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "10") int size) {

        StoreCursor cursor = (keyword != null || storeId != null)
                ? new StoreCursor(keyword, storeId)
                : null;

        List<StoreResponse> stores = storeServiceV2.findStoresByKeyword(cursor, size);
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @Operation(summary = "내 스토어 목록 조회", description = "현재 로그인한 유저의 스토어들을 반환함")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> findMyStores(
            @AuthenticationPrincipal AuthUser authUser) {

        List<StoreResponse> stores = storeServiceV2.findMyStores(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @Operation(summary = "내 간단한 스토어 목록 조회", description = "스토어 id와 이름만 반환")
    @GetMapping("/my/simple")
    public ResponseEntity<ApiResponse<List<StoreSimpleResponse>>> findMySimpleStores(
            @AuthenticationPrincipal AuthUser authUser) {

        List<StoreSimpleResponse> stores = storeServiceV2.findMySimpleStores(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @Operation(summary = "스토어 단건 조회", description = "스토어 ID로 상세 정보를 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> findStore(
            @PathVariable Long storeId) {

        StoreResponse store = storeServiceV2.findStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(store));
    }

    @Operation(summary = "스토어 수정", description = "사용자가 자신의 스토어 정보를 수정함")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long storeId,
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal AuthUser authUser) {

        StoreResponse updatedStore = storeServiceV2.updateStore(storeId, request, authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(updatedStore));
    }

    @Operation(summary = "스토어 삭제", description = "사용자가 자신의 스토어를 삭제함")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal AuthUser authUser) {

        storeServiceV2.deleteStore(storeId, authUser.getId());
        return ResponseEntity.noContent().build();
    }
}
