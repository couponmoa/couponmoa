package com.couponmoa.backend.domain.user.service.v1;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.domain.user.dto.request.SigninRequest;
import com.couponmoa.backend.domain.user.dto.request.SignupRequest;
import com.couponmoa.backend.domain.user.dto.response.TokenResponse;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.couponmoa.backend.domain.user.enums.UserRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private SigninRequest signinRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("test@example.com", "password", "nickname", "ROLE_USER");
        signinRequest = new SigninRequest("test@example.com", "password");
        user = new User("test@example.com", "encodedPassword", "nickname", ROLE_USER);
    }

    @Nested
    class signup {
        @Test
        void 이미_존재하는_계정으로_회원가입_시_예외() {
            given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(true);

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.signup(signupRequest));
            assertEquals(ErrorCode.EMAIL_ALREADY_EXIST.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 탈퇴한_계정으로_회원가입_시_예외() {
            given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(false);
            given(userRepository.existsByEmailAndDeletedAtIsNotNull(anyString())).willReturn(true);

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.signup(signupRequest));
            assertEquals(ErrorCode.EMAIL_ALREADY_DELETED.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 회원가입_성공() {
            given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(false);
            given(userRepository.existsByEmailAndDeletedAtIsNotNull(anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(user);

            authService.signup(signupRequest);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    class signin {
        @Test
        void 사용자_정보_불일치_시_예외() {
            given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.signin(signinRequest));
            assertEquals(ErrorCode.USER_NOT_FOUND.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 비밀번호_불일치_시_예외() {
            given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.signin(signinRequest));
            assertEquals(ErrorCode.INVALID_PASSWORD.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 로그인_성공() {
            given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
            given(jwtUtil.createAccessToken(any(), anyString(), any(UserRole.class))).willReturn("accessToken");
            given(jwtUtil.createRefreshToken(any(), anyString(), any(UserRole.class))).willReturn("refreshToken");

            TokenResponse tokenResponse = authService.signin(signinRequest);

            assertEquals("accessToken", tokenResponse.getAccessToken());
            assertEquals("refreshToken", tokenResponse.getRefreshToken());
        }
    }

    @Nested
    class refreshToken {
        @Test
        void 사용자_정보_불일치_시_예외() {
            String userId = "1";
            String refreshToken = "refreshToken";
            Claims claims = mock(Claims.class);

            given(jwtUtil.extractClaims(anyString())).willReturn(claims);
            given(claims.getSubject()).willReturn(userId);
            doNothing().when(jwtUtil).validateToken(anyString(), anyString());
            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willThrow(
                    new ApplicationException(ErrorCode.USER_NOT_FOUND));

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.refreshToken(refreshToken));
            assertEquals(ErrorCode.USER_NOT_FOUND.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 리프레쉬_토큰_생성_성공() {
            String userId = "1";
            String refreshToken = "refreshToken";
            String accessToken = "accessToken";
            Claims claims = mock(Claims.class);

            given(jwtUtil.extractClaims(anyString())).willReturn(claims);
            given(claims.getSubject()).willReturn(userId);
            doNothing().when(jwtUtil).validateToken(anyString(), anyString());
            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(user);
            given(jwtUtil.createAccessToken(any(), anyString(), any(UserRole.class))).willReturn(accessToken);

            TokenResponse tokenResponse = authService.refreshToken(refreshToken);

            assertEquals(refreshToken, tokenResponse.getRefreshToken());
            assertEquals(accessToken, tokenResponse.getAccessToken());
        }
    }
}
