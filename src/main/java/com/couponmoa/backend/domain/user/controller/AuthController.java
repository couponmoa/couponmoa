package com.couponmoa.backend.domain.user.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.request.SigninRequest;
import com.couponmoa.backend.domain.user.dto.request.SignupRequest;
import com.couponmoa.backend.domain.user.dto.response.TokenResponse;
import com.couponmoa.backend.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입 완료"));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SigninRequest signinRequest) {
        TokenResponse response = authService.signin(signinRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인 완료"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        TokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response, "토큰 재발급 완료"));
    }
}
