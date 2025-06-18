package org.example.hugmeexp.global.infra.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.entity.enumeration.UserRole;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.exception.InvalidRefreshTokenException;
import org.example.hugmeexp.global.infra.auth.exception.TokenReuseDetectedException;
import org.example.hugmeexp.global.infra.auth.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService
{
    private final RedisSessionService redisSessionService;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    // 리프레시 토큰 저장
    public void saveRefreshToken(String username, String refreshToken, long refreshTokenValidityMs) {
        redisTemplate.opsForValue().set("refresh:" + username, refreshToken, Duration.ofMillis(refreshTokenValidityMs));
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh:" + username);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }

    // 액세스 토큰 생성
    public String createAccessToken(String username, UserRole role) {
        return jwtTokenProvider.createAccessToken(username, "ROLE_" + role.name());
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username, UserRole role) {
        return jwtTokenProvider.createRefreshToken(username, role);
    }

    // 토큰 유효기간 조회
    public long getTokenRemainingTimeMillis(String token) {
        return jwtTokenProvider.getTokenRemainingTimeMillis(token);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        return jwtTokenProvider.validate(token);
    }

    // 토큰에서 username 추출
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsername(token);
    }

    // 액세스 토큰 블랙리스트 추가
    public void blacklistAccessToken(String accessToken) {
        long remainingTime = jwtTokenProvider.getTokenRemainingTimeMillis(accessToken);
        if (remainingTime > 0) redisSessionService.blacklistAccessToken(accessToken, remainingTime);
    }

    // 로그아웃
    public boolean logout(String accessToken) {
        try {
            // 토큰 검증 및 username 추출
            if (!jwtTokenProvider.validate(accessToken)) {
                log.warn("Failed to validate token during logout - accessToken: {}", accessToken.substring(0, 10) + "...");
                return false;
            }

            String username = jwtTokenProvider.getUsername(accessToken);
            if (username == null) {
                log.warn("Failed to extract username during logout - accessToken: {}", accessToken.substring(0, 10) + "...");
                return false;
            }

            // 리프레시 토큰 삭제
            deleteRefreshToken(username);

            // 액세스 토큰 블랙리스트 추가
            long remainingTime = jwtTokenProvider.getTokenRemainingTimeMillis(accessToken);
            if (remainingTime > 0) redisSessionService.blacklistAccessToken(accessToken, remainingTime);

            log.info("Logout successful - username: {}", username);
            return true;
        }
        catch (Exception e) {
            log.error("Logout process failed: {}", e.getMessage(), e);
            return false;
        }
    }

    // 리프레시 토큰 재발급
    public AuthResponse refreshTokens(String refreshToken) {

        if(!jwtTokenProvider.validate(refreshToken)) throw new InvalidRefreshTokenException();

        // 2. 재사용 여부 검사
        if (jwtTokenProvider.isRefreshTokenRevoked(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);
            if (username != null) deleteRefreshToken(username); // 저장된 refresh 토큰 삭제
            throw new TokenReuseDetectedException();
        }

        // 3. 사용자 정보 추출
        String username = jwtTokenProvider.getUsername(refreshToken);
        if (username == null) throw new InvalidRefreshTokenException();

        String role = jwtTokenProvider.getRole(refreshToken);
        if (role == null) throw new InvalidRefreshTokenException();

        // 4. 기존 리프레시 토큰 무효화 처리
        jwtTokenProvider.revokeRefreshToken(refreshToken);

        // 5. 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(username, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username, UserRole.valueOf(role.replace("ROLE_", "")));

        // 6. 저장
        saveRefreshToken(username, newRefreshToken, jwtTokenProvider.getTokenRemainingTimeMillis(newRefreshToken));
        log.info("Refresh token reissued successfully - username: {}", username);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}