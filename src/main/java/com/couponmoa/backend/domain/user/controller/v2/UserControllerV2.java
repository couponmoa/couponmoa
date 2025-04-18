package com.couponmoa.backend.domain.user.controller.v2;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.service.v2.UserServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 관리 API V2", description = "CDN을 활용한 프로필 이미지 포함 사용자 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
public class UserControllerV2 {
    private final UserServiceV2 userServiceV2;

    @Operation(summary = "CDN 사용 본인 정보 조회", description = "본인의 사용자 정보를 확인함")
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> findUser(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse userResponse = userServiceV2.findUser(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(userResponse, "회원 조회 완료"));
    }
}
