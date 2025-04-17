package com.couponmoa.backend.domain.user.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.dto.request.UserDeleteRequest;
import com.couponmoa.backend.domain.user.dto.request.UserUpdatePasswordRequest;
import com.couponmoa.backend.domain.user.dto.request.UserUpdateRequest;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.service.UserServiceV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 관리 API", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerV1 {

    private final UserServiceV1 userServiceV1;

    @Operation(summary = "본인 정보 조회", description = "본인의 사용자 정보를 확인함")
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> findUser(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse userResponse = userServiceV1.findUser(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(userResponse, "회원 조회 완료"));
    }

    @Operation(summary = "사용자 정보 수정", description = "비밀번호를 제외한 본인의 정보를 수정함")
    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userServiceV1.updateUser(authUser.getId(), userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 완료"));
    }

    @Operation(summary = "비밀번호 변경", description = "본인의 비밀번호를 변경함")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        userServiceV1.updateUserPassword(authUser.getId(), userUpdatePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 수정 완료"));
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호 입력시 회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserDeleteRequest userDeleteRequest) {
        userServiceV1.deleteUser(authUser.getId(), userDeleteRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("탈퇴 완료"));
    }
}
