package org.example.hugmeexp.global.auth.service;

import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.enums.UserRole;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.example.hugmeexp.global.infra.auth.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;           // when(), verify(), any(), eq(), etc.
import static org.mockito.BDDMockito.*;        // given(), willReturn(), willThrow(), etc.
import static org.assertj.core.api.Assertions.*;  // assertThat(), assertThatThrownBy(), etc.

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CredentialService credentialService;
    @Mock private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입에 성공하면 accessToken과 refreshToken이 발급된다")
    void shouldRegisterAndReturnTokens() {
        // given
        RegisterRequest request = new RegisterRequest("testuser", "raw1234!", "홍길동", "010-1234-5678");

        User mockUser = User.createUser("testuser", "encodedPw", "홍길동", "010-1234-5678");

        given(credentialService.registerNewUser(request)).willReturn(mockUser);
        given(tokenService.createAccessToken("testuser", mockUser.getRole())).willReturn("access-token");
        given(tokenService.createRefreshToken("testuser", mockUser.getRole())).willReturn("refresh-token");
        given(tokenService.getTokenRemainingTimeMillis("refresh-token")).willReturn(3600000L);

        // when
        AuthResponse response = authService.registerAndAuthenticate(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(tokenService).saveRefreshToken("testuser", "refresh-token", 3600000L);
    }

    @Test
    @DisplayName("로그인에 성공하면 기존 리프레시 토큰을 무효화하고 새로운 토큰을 발급한다")
    void shouldLoginAndReturnNewTokens() {
        // given
        LoginRequest request = new LoginRequest("testuser", "raw1234!");

        User mockUser = User.createUser("testuser", "encodedPw", "홍길동", "010-1234-5678");

        given(credentialService.login(request)).willReturn(mockUser);
        given(tokenService.getRefreshToken("testuser")).willReturn("old-refresh-token");
        willDoNothing().given(tokenService).revokeRefreshToken("old-refresh-token");

        given(tokenService.createAccessToken("testuser", mockUser.getRole())).willReturn("new-access-token");
        given(tokenService.createRefreshToken("testuser", mockUser.getRole())).willReturn("new-refresh-token");
        given(tokenService.getTokenRemainingTimeMillis("new-refresh-token")).willReturn(3600000L);

        // when
        AuthResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(tokenService).revokeRefreshToken("old-refresh-token");
        verify(tokenService).saveRefreshToken("testuser", "new-refresh-token", 3600000L);
    }
}

