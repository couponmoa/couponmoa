package com.couponmoa.backend.domain.user.controller.v1;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.service.v1.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "사용자 프로필 사진 API", description = "사용자 프로필 사진 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/image")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "프로필 사진 등록", description = "사용자의 프로필 사진을 등록함")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> updateUserImage(
            @AuthenticationPrincipal AuthUser authUser, @RequestPart(value = "image") MultipartFile image) throws IOException {
        userProfileService.updateUserImage(authUser.getId(), image);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 사진 등록 완료"));
    }

    @Operation(summary = "프로필 사진 삭제", description = "사용자의 프로필 사진을 삭제함")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> updateUserImage(
            @AuthenticationPrincipal AuthUser authUser) {
        userProfileService.deleteUserImage(authUser.getId());
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 사진 삭제 완료"));
    }
}
