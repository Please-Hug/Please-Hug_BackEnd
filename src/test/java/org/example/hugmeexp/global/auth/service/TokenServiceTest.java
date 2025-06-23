package org.example.hugmeexp.global.auth.service;

import org.example.hugmeexp.domain.user.enums.UserRole;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.exception.InvalidRefreshTokenException;
import org.example.hugmeexp.global.infra.auth.exception.TokenReuseDetectedException;
import org.example.hugmeexp.global.infra.auth.jwt.JwtTokenProvider;
import org.example.hugmeexp.global.infra.auth.service.RedisSessionService;
import org.example.hugmeexp.global.infra.auth.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.*;           // when(), verify(), any(), eq(), etc.
import static org.mockito.BDDMockito.*;        // given(), willReturn(), willThrow(), etc.
import static org.assertj.core.api.Assertions.*;  // assertThat(), assertThatThrownBy(), etc.

@DisplayName("TokenServiceTest")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock private RedisSessionService redisSessionService;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks private TokenService tokenService;

    @Test
    @DisplayName("유효한 리프레시 토큰이 주어지면 기존 토큰을 무효화하고 새 토큰을 발급 및 저장한다")
    void reissueNewTokens_givenValidRefreshToken() {
        // given
        String accessToken = "access.token";
        String refreshToken = "refresh.token";
        String username = "user123";
        String role = "ROLE_USER";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        willDoNothing().given(jwtTokenProvider).validateAccessTokenForReissue(accessToken);
        given(jwtTokenProvider.validate(refreshToken)).willReturn(true);
        given(jwtTokenProvider.isRefreshTokenRevoked(refreshToken)).willReturn(false);
        given(jwtTokenProvider.getUsername(refreshToken)).willReturn(username);
        given(jwtTokenProvider.getRole(refreshToken)).willReturn(role);
        willDoNothing().given(jwtTokenProvider).revokeRefreshToken(refreshToken);
        given(jwtTokenProvider.createAccessToken(username, role)).willReturn(newAccessToken);
        given(jwtTokenProvider.createRefreshToken(eq(username), any(UserRole.class))).willReturn(newRefreshToken);
        given(jwtTokenProvider.getTokenRemainingTimeMillis(newRefreshToken)).willReturn(3600000L);

        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOps);

        // when
        AuthResponse result = tokenService.refreshTokens(accessToken, refreshToken);

        // then
        assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);

        verify(valueOps).set(eq("refresh:" + username), eq(newRefreshToken), eq(Duration.ofMillis(3600000L)));
    }

    @Test
    @DisplayName("재사용된 리프레시 토큰이면 예외를 발생시키고 기존 리프레시 토큰을 삭제한다")
    void shouldThrow_whenRefreshTokenAlreadyUsed() {
        // given
        String accessToken = "access.token";
        String refreshToken = "used.refresh.token";
        String username = "user123";

        willDoNothing().given(jwtTokenProvider).validateAccessTokenForReissue(accessToken);
        given(jwtTokenProvider.validate(refreshToken)).willReturn(true);
        given(jwtTokenProvider.isRefreshTokenRevoked(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUsername(refreshToken)).willReturn(username);

        // when & then
        assertThatThrownBy(() -> tokenService.refreshTokens(accessToken, refreshToken))
                .isInstanceOf(TokenReuseDetectedException.class);

        verify(redisTemplate).delete("refresh:" + username);
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이면 예외가 발생한다")
    void shouldThrow_whenRefreshTokenInvalid() {
        // given
        String accessToken = "access.token";
        String refreshToken = "invalid.token";

        willDoNothing().given(jwtTokenProvider).validateAccessTokenForReissue(accessToken);
        given(jwtTokenProvider.validate(refreshToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> tokenService.refreshTokens(accessToken, refreshToken))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("로그아웃 시 refresh 토큰을 삭제하고 access 토큰을 블랙리스트에 추가한다")
    void shouldLogoutAndBlacklistAccessToken() {
        // given
        String accessToken = "logout.token";
        String username = "user123";
        long remainingTime = 300000L;

        given(jwtTokenProvider.getUsername(accessToken)).willReturn(username);
        given(jwtTokenProvider.getTokenRemainingTimeMillis(accessToken)).willReturn(remainingTime);

        // when
        tokenService.logout(accessToken);

        // then
        verify(redisTemplate).delete("refresh:" + username);
        verify(redisSessionService).blacklistAccessToken(accessToken, remainingTime);
    }
}