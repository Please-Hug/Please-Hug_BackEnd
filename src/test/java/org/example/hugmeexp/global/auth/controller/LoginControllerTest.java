package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*
    테스트를 위해서 시크릿키를 넣었음
    프로덕션 환경에선 다른 시크릿키를 사용해야함
*/
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("로그인 컨트롤러 테스트")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "jwt.secret=aHR0cHM6Ly9naXRodWIuY29tL3NldW5nd29vay9qd3QtYXBpLXNlcnZlci1zYW1wbGUteW91LWNhbi11c2UtdGhpcy1sb25nLXNlY3JldC1rZXktZm9yLWVuY3J5cHRpb24K",
        "jwt.access-token-expiration=10000",
        "jwt.refresh-token-expiration=60000"
})
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private UserService userService;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() {
        RegisterRequest request = new RegisterRequest("testuser", "testpassword1!", "홍길동", "010-1234-5678");
        credentialService.registerNewUser(request);
    }

    @AfterEach
    void tearDown() {
        userService.deleteByUsername("testuser");
    }

    @Test
    @DisplayName("로그인 성공시 토큰이 응답된다.")
    void shouldReturnTokens_whenLoginIsSuccessful() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "testpassword1!");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 아이디를 입력하면 실패한다.")
    void shouldFailLogin_whenUserDoesNotExist() throws Exception {
        // given
        LoginRequest request = new LoginRequest("notexist", "testpassword1!");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("틀린 비밀번호를 입력하면 실패한다.")
    void shouldFailLogin_whenPasswordIsIncorrect() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
                .andExpect(status().isUnauthorized());
    }
}