package org.example.hugmeexp.global.infra.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.service.UserService;
import org.example.hugmeexp.global.entity.User;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.*;
import org.example.hugmeexp.global.infra.auth.exception.InvalidAccessTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    AuthService는 중재자 패턴(Mediator Pattern)을 구현한 서비스 클래스로, 인증 관련 모든 비즈니스 로직을 통합 관리
    세부 구현은 UserService와 TokenService에 위임
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserService userService;
    private final TokenService tokenService;

    // 회원가입
    @Transactional
    public AuthResponse registerAndAuthenticate(RegisterRequest request)
    {
        // 1. 사용자 등록
        User user = userService.registerNewUser(request);

        // 2. 토큰 생성 및 저장
        String accessToken = tokenService.createAccessToken(user.getUsername(), user.getRole());
        String refreshToken = tokenService.createRefreshToken(user.getUsername(), user.getRole());

        // 3. 리프레시 토큰 저장
        tokenService.saveRefreshToken(user.getUsername(), refreshToken, tokenService.getTokenRemainingTimeMillis(refreshToken));

        // 4. 액세스 토큰, 리프레시 토큰 리턴
        return new AuthResponse(accessToken, refreshToken);
    }

    // 로그인
    public AuthResponse login(LoginRequest request) {
        // 1. 사용자 검증
        User user = userService.login(request);

        // 2. 토큰 생성 및 저장
        String accessToken = tokenService.createAccessToken(user.getUsername(), user.getRole());
        String refreshToken = tokenService.createRefreshToken(user.getUsername(), user.getRole());
        tokenService.saveRefreshToken(user.getUsername(), refreshToken, tokenService.getTokenRemainingTimeMillis(refreshToken));

        // 4. 액세스 토큰, 리프레시 토큰 리턴
        return new AuthResponse(accessToken, refreshToken);
    }

    // 리프레시 토큰 재발급
    public AuthResponse refreshTokens(String refreshToken) {
        return tokenService.refreshTokens(refreshToken);
    }

    // 로그아웃 처리
    public boolean logout(String accessToken) {
        return tokenService.logout(accessToken);
    }

    // 액세스 토큰 무효화 처리 (관리자용)
    public void revokeAccessToken(String accessToken) {
        if (!tokenService.validateToken(accessToken)) throw new InvalidAccessTokenException();

        // 토큰 블랙리스트 추가 및 리프레시 토큰 삭제
        String username = tokenService.getUsernameFromToken(accessToken);
        if (username != null) tokenService.deleteRefreshToken(username);

        tokenService.blacklistAccessToken(accessToken);
    }
}