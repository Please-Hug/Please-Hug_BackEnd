package org.example.hugmeexp.global.infra.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RefreshRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    AuthService는 중재자 패턴(Mediator Pattern)을 구현한 서비스 클래스로, 회원가입, 로그인, 인증 관련 모든 비즈니스 로직을 통합 관리
    세부 구현은 CredentialService와 TokenService에 위임
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService
{
    private final CredentialService credentialService;
    private final TokenService tokenService;

    // 회원가입
    @Transactional
    public AuthResponse registerAndAuthenticate(RegisterRequest request)
    {
        // 1. 사용자 등록
        User user = credentialService.registerNewUser(request);

        // 2. 토큰 생성 및 저장
        String accessToken = tokenService.createAccessToken(user.getUsername(), user.getRole());
        String refreshToken = tokenService.createRefreshToken(user.getUsername(), user.getRole());

        // 3. 리프레시 토큰 저장
        tokenService.saveRefreshToken(user.getUsername(), refreshToken, tokenService.getTokenRemainingTimeMillis(refreshToken));

        // 4. 액세스 토큰, 리프레시 토큰 리턴
        log.info("Sign-up successful - user: {}({})", user.getUsername(), user.getName());
        return new AuthResponse(accessToken, refreshToken);
    }

    // 로그인
    public AuthResponse login(LoginRequest request) {
        // 1. 사용자 검증
        User user = credentialService.login(request);

        // 2. 기존 리프레시 토큰이 있다면 무효화
        String oldRefreshToken = tokenService.getRefreshToken(user.getUsername());
        if (oldRefreshToken != null) {
            tokenService.revokeRefreshToken(oldRefreshToken);
        }

        // 3. 토큰 생성 및 저장
        String accessToken = tokenService.createAccessToken(user.getUsername(), user.getRole());
        String refreshToken = tokenService.createRefreshToken(user.getUsername(), user.getRole());
        tokenService.saveRefreshToken(user.getUsername(), refreshToken, tokenService.getTokenRemainingTimeMillis(refreshToken));

        // 4. 액세스 토큰, 리프레시 토큰 리턴
        log.info("Login successful - user: {}({})", user.getUsername(), user.getName());
        return new AuthResponse(accessToken, refreshToken);
    }

    // 리프레시 토큰 재발급
    public AuthResponse refreshTokens(RefreshRequest request) {
        return tokenService.refreshTokens(request.getAccessToken(), request.getRefreshToken());
    }

    // 로그아웃 처리
    public void logout(String accessToken) {
        tokenService.logout(accessToken);
    }

    // 액세스 또는 리프레시 토큰 유효성 검사
    public boolean validateToken(String token) {
        return tokenService.validateToken(token);
    }

    // 액세스 또는 리프레시 토큰에서 username을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        return tokenService.getUsernameFromToken(token);
    }
}