package com.couponmoa.backend.config;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisService redisService;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Long ACCESS_TOKEN_TIME = 60 * 30 * 1000L; // 30분
    private static final Long REFRESH_TOKEN_TIME = 60 * 60 * 1000L * 24; // 1일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long userId, String email, UserRole userRole, Long validTime) {
        Date date = new Date();
        log.info("JWT 생성 - userId: {}, email: {}, 역할: {}", userId, email, userRole.name()); //

        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("userRole", userRole.getUserRole())
                        .expiration(new Date(date.getTime() + validTime))
                        .issuedAt(date)
                        .signWith(key)
                        .compact();
    }

    public String createAccessToken(Long userId, String email, UserRole userRole) {
        return this.createToken(userId, email, userRole, ACCESS_TOKEN_TIME);
    }

    public String createRefreshToken(Long userId, String email, UserRole userRole) {
        String refreshToken =  this.createToken(userId, email, userRole, REFRESH_TOKEN_TIME);
        redisService.save(userId.toString(), refreshToken, Duration.ofMillis(REFRESH_TOKEN_TIME));
        return refreshToken;
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new ApplicationException(ErrorCode.TOKEN_NOT_FOUND);
    }

    public Claims extractClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        log.info("JWT 검증 완료 - userRole: {}", claims.get("userRole"));
        return claims;
    }

    public void validateToken(String refreshToken, String userId) {
        try {
            String redisToken = redisService.get(userId);
            if (!refreshToken.equals(substringToken(redisToken))) {
                throw new ApplicationException(ErrorCode.INVALID_JWT);
            }
            Jwts.parser().verifyWith(key).build().parse(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(ErrorCode.EXPIRED_JWT);
        } catch (JwtException e) {
            throw new ApplicationException(ErrorCode.INVALID_JWT);
        }
    }

}