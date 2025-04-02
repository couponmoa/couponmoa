package com.couponmoa.backend.domain.user.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.user.dto.request.SigninRequest;
import com.couponmoa.backend.domain.user.dto.request.SignupRequest;
import com.couponmoa.backend.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SigninRequest signinRequest) {
        String token = authService.signin(signinRequest);
        return ResponseEntity.ok(ApiResponse.success(token, "로그인 완료"));
    }
}
