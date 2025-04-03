package com.couponmoa.backend.domain.subscribe.userstore.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.subscribe.userstore.dto.response.FindStoreSubscribeListResponse;
import com.couponmoa.backend.domain.subscribe.userstore.service.UserStoreSubscribeService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "가게 구독 API", description = "가게 구독 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class UserStoreSubscribeController {

    private final UserStoreSubscribeService userStoreSubServ;

    @Operation(summary = "가게 구독", description = "유저가 특정 가게을 구독함")
    @PostMapping("/{storeId}/subscriptions")
    public ResponseEntity<ApiResponse<Void>> subscribeStore(@AuthenticationPrincipal AuthUser user,
                                                             @Parameter(description = "구독할 가게 id", required = true)
                                                             @PathVariable Long storeId) {
        userStoreSubServ.subscribeStore(user.getId(), storeId);

        return ResponseEntity.ok(ApiResponse.success(storeId + "번 가게 구독 완료"));
    }

    @Operation(summary = "가게 구독 취소", description = "유저가 특정 가게 구독을 취소함")
    @PostMapping("/{storeId}/unsubscriptions")
    public ResponseEntity<ApiResponse<Void>> unsubscribeStore(@AuthenticationPrincipal AuthUser user,
                                                               @Parameter(description = "구독할 가게 id", required = true)
                                                               @PathVariable Long storeId) {
        userStoreSubServ.unSubscribeCoupon(user.getId(), storeId);

        return ResponseEntity.ok(ApiResponse.success(storeId + "번 가게 구독 취소"));
    }

    @Operation(summary = "내 가게 구독 목록 확인", description = "가게 구독 목록 확인")
    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponse<List<FindStoreSubscribeListResponse>>> findStoreSubscribeList(@AuthenticationPrincipal AuthUser user,
                                                                                               @RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "10") int size) {
        List<FindStoreSubscribeListResponse> subscribeList = userStoreSubServ.findSubscribeList(user.getId(), page, size);

        return ResponseEntity.ok(ApiResponse.success(subscribeList, "가게 구독 목록 조회 성공"));
    }

    @Operation(summary = "알림서비스", description = "가게 쿠폰이 새로 생기면 이메일을 알림을 보냄")
    @PostMapping("/{storeId}/alert")
    public ResponseEntity<ApiResponse<List<String>>> sendAlert(@PathVariable Long storeId) {
        List<String> emailList = userStoreSubServ.sendAlert(storeId);

        return ResponseEntity.ok(ApiResponse.success(emailList));
    }
}
