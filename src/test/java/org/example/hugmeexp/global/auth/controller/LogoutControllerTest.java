package org.example.hugmeexp.global.auth.controller;

import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("로그아웃 컨트롤러 테스트")
@TestPropertySource(properties = {
        "jwt.secret=aHR0cHM6Ly9naXRodWIuY29tL3NldW5nd29vay9qd3QtYXBpLXNlcnZlci1zYW1wbGUteW91LWNhbi11c2UtdGhpcy1sb25nLXNlY3JldC1rZXktZm9yLWVuY3J5cHRpb24K",
        "jwt.access-token-expiration=10000",
        "jwt.refresh-token-expiration=60000"
})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private AuthService authService;
    @Autowired private CredentialService credentialService;
    @Autowired private UserService userService;

    private final String username = "logoutuser";
    private final String phone = "010-2222-3333";
    private final String password = "logout123!";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @AfterEach
    void tearDown() {
        userService.deleteByUsername(username);
    }

    @Test
    @DisplayName("액세스 토큰을 포함한 요청으로 로그아웃하면 200 상태코드를 반환한다.")
    void shouldReturnOk_whenLogoutSuccessfully() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest(username, password, "강호동", phone);
        credentialService.registerNewUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthResponse tokens = authService.login(loginRequest);

        // when
        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                // then
                .andExpect(status().isOk());
    }
}