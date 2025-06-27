package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RefreshRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("리프레시 토큰 컨트롤러 테스트")
@TestPropertySource(properties = {
        "jwt.secret=aHR0cHM6Ly9naXRodWIuY29tL3NldW5nd29vay9qd3QtYXBpLXNlcnZlci1zYW1wbGUteW91LWNhbi11c2UtdGhpcy1sb25nLXNlY3JldC1rZXktZm9yLWVuY3J5cHRpb24K",
        "jwt.access-token-expiration=1000",
        "jwt.refresh-token-expiration=60000"
})
class RefreshControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;
    @Autowired private CredentialService credentialService;
    @Autowired private UserService userService;

    private final String username = "refreshuser";
    private final String phone = "010-3333-4444";
    private final String password = "refresh123!";

    @AfterEach
    void tearDown() {
        try {
            userService.deleteByUsername("refreshuser");
        } catch (UserNotFoundException e) {

        }
    }

    @Test
    @DisplayName("유효한 리프레시 토큰이 주어지면 새로운 access, refresh 토큰을 반환한다.")
    void shouldReturnNewTokens_whenRefreshTokenIsValid() throws Exception {
        // given
        credentialService.registerNewUser(new RegisterRequest(username, password, "이순신", phone));
        AuthResponse tokens = authService.login(new LoginRequest(username, password));

        RefreshRequest request = new RefreshRequest(tokens.getAccessToken(), tokens.getRefreshToken());

        Thread.sleep(1100);  // 액세스 토큰 만료 기다림

        // when
        ResultActions result = mockMvc.perform(post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이 주어지면 401 상태코드를 반환한다.")
    void shouldReturnUnauthorized_whenRefreshTokenIsInvalid() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest("fake.access.token", "fake.refresh.token");

        // when
        ResultActions result = mockMvc.perform(post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("아직 만료되지 않은 액세스 토큰으로 리프레시 요청 시 400 상태코드를 반환한다.")
    void shouldReturnBadRequest_whenAccessTokenIsStillValid() throws Exception {
        // given
        credentialService.registerNewUser(new RegisterRequest(username, password, "이순신", phone));
        AuthResponse tokens = authService.login(new LoginRequest(username, password));

        RefreshRequest request = new RefreshRequest(tokens.getAccessToken(), tokens.getRefreshToken());

        // when
        ResultActions result = mockMvc.perform(post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }
}