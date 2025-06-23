package org.example.hugmeexp.global.auth.service.CredentialService;

import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.exception.LoginFailedException;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.*;        // given(), willReturn(), willThrow(), etc.
import static org.assertj.core.api.Assertions.*;  // assertThat(), assertThatThrownBy(), etc.

@DisplayName("CredentialService 로그인 테스트")
@ExtendWith(MockitoExtension.class)
class CredentialServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CredentialService credentialService;

    @Test
    @DisplayName("로그인 성공 시 User를 반환한다")
    void shouldLoginSuccessfully() {
        // given
        LoginRequest request = new LoginRequest("testuser", "correctPassword");
        User mockUser = User.createUser("testuser", "encodedPassword", "홍길동", "010-1234-5678");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("correctPassword", "encodedPassword")).willReturn(true);

        // when
        User loggedInUser = credentialService.login(request);

        // then
        assertThat(loggedInUser).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시 LoginFailedException을 던진다")
    void shouldFailLoginWhenUserNotFound() {
        // given
        LoginRequest request = new LoginRequest("nonexistent", "password");

        given(userRepository.findByUsername("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> credentialService.login(request))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 LoginFailedException을 던진다")
    void shouldFailLoginWhenPasswordIncorrect() {
        // given
        LoginRequest request = new LoginRequest("testuser", "wrongPassword");
        User mockUser = User.createUser("testuser", "encodedPassword", "홍길동", "010-1234-5678");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> credentialService.login(request))
                .isInstanceOf(LoginFailedException.class);
    }
}
