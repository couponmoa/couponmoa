package com.couponmoa.backend.domain.user.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.domain.user.dto.request.SigninRequest;
import com.couponmoa.backend.domain.user.dto.request.SignupRequest;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupRequest signupRequest) {
        String email = signupRequest.getEmail();

        if(userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        if(userRepository.existsByEmailAndDeletedAtIsNotNull(email)) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_DELETED);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User user = new User(
                email,
                encodedPassword,
                signupRequest.getNickname(),
                UserRole.of(signupRequest.getUserRole())
        );

        userRepository.save(user);
    }

    @Transactional
    public String signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(signinRequest.getEmail())
                .orElseThrow(()-> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(signinRequest.getPassword(),user.getPassword())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        return jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());
    }
}
