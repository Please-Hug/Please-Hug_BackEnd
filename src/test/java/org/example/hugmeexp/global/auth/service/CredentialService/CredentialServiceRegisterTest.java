package org.example.hugmeexp.global.auth.service.CredentialService;

import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.PhoneNumberDuplicatedException;
import org.example.hugmeexp.domain.user.exception.UsernameDuplicatedException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;           // when(), verify(), any(), eq(), etc.
import static org.mockito.BDDMockito.*;        // given(), willReturn(), willThrow(), etc.
import static org.assertj.core.api.Assertions.*;  // assertThat(), assertThatThrownBy(), etc.

@DisplayName("CredentialService 회원가입 테스트")
@ExtendWith(MockitoExtension.class)
class CredentialServiceRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CredentialService credentialService;

    @Test
    @DisplayName("회원가입 성공 시 유저가 저장되고 암호화된 비밀번호를 가진다")
    void shouldRegisterUserSuccessfully() {
        // given
        RegisterRequest request = new RegisterRequest(
                "testuser", "rawPassword123!", "홍길동", "010-1234-5678"
        );

        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.existsByPhoneNumber("010-1234-5678")).willReturn(false);
        given(passwordEncoder.encode("rawPassword123!")).willReturn("encodedPassword123!");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User savedUser = credentialService.registerNewUser(request);

        // then
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getName()).isEqualTo("홍길동");
        assertThat(savedUser.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123!");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 사용자명이 존재하면 UsernameDuplicatedException을 던진다")
    void shouldFailRegistrationWhenUsernameIsDuplicated() {
        // given
        RegisterRequest request = new RegisterRequest("dupUser", "pw123!", "name", "010-0000-0000");
        given(userRepository.existsByUsername("dupUser")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> credentialService.registerNewUser(request))
                .isInstanceOf(UsernameDuplicatedException.class);
    }

    @Test
    @DisplayName("중복된 전화번호가 존재하면 PhoneNumberDuplicatedException을 던진다")
    void shouldFailRegistrationWhenPhoneNumberIsDuplicated() {
        // given
        RegisterRequest request = new RegisterRequest("newuser", "pw123!", "name", "010-0000-0000");
        given(userRepository.existsByUsername("newuser")).willReturn(false);
        given(userRepository.existsByPhoneNumber("010-0000-0000")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> credentialService.registerNewUser(request))
                .isInstanceOf(PhoneNumberDuplicatedException.class);
    }
}

