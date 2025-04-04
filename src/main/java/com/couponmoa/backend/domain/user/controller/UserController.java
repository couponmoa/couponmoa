package com.couponmoa.backend.domain.user.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.dto.request.UserDeleteRequest;
import com.couponmoa.backend.domain.user.dto.request.UserUpdatePasswordRequest;
import com.couponmoa.backend.domain.user.dto.request.UserUpdateRequest;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> findUser(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse userResponse = userService.findUser(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success(userResponse, "회원 조회 완료"));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(authUser.getId(), userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 완료"));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        userService.updateUserPassword(authUser.getId(), userUpdatePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 수정 완료"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserDeleteRequest userDeleteRequest) {
        userService.deleteUser(authUser.getId(), userDeleteRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("탈퇴 완료"));
    }
}
