package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("회원가입 컨트롤러 테스트")
@TestPropertySource(properties = {
        "jwt.secret=aHR0cHM6Ly9naXRodWIuY29tL3NldW5nd29vay9qd3QtYXBpLXNlcnZlci1zYW1wbGUteW91LWNhbi11c2UtdGhpcy1sb25nLXNlY3JldC1rZXktZm9yLWVuY3J5cHRpb24K",
        "jwt.access-token-expiration=10000",
        "jwt.refresh-token-expiration=60000"
})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegisterControllerTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CredentialService credentialService;
    @Autowired private UserService userService;

    private final String username = "newuser";
    private final String phone = "010-1111-2222";

    @AfterEach
    void tearDown() {
        try {
            userService.deleteByUsername(username);
        } catch (UserNotFoundException e) {
            System.out.println("테스트 사용자 삭제 생략: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("회원가입에 성공하면 200 상태코드와 토큰 정보를 반환한다.")
    void shouldReturnOkAndTokens_whenRegisterUserSuccessfully() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest(username, "validPass1!", "홍길동", phone);

        // when
        ResultActions result = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("중복된 사용자명으로 회원가입하면 409 상태코드를 반환한다.")
    void shouldReturnConflict_whenUsernameIsDuplicated() throws Exception {
        // given
        RegisterRequest original = new RegisterRequest(username, "validPass1!", "홍길동", "010-9999-0000");
        credentialService.registerNewUser(original);

        RegisterRequest duplicateUsername = new RegisterRequest(username, "validPass1!", "홍길동", "010-8888-0000");

        // when
        ResultActions result = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUsername)));

        // then
        result.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("중복된 휴대폰 번호로 회원가입 요청을 하면 409 상태코드를 반환한다.")
    void shouldReturnConflict_whenPhoneNumberIsDuplicated() throws Exception {
        // given
        RegisterRequest original = new RegisterRequest(username, "validPass1!", "홍길동", phone);
        credentialService.registerNewUser(original);

        RegisterRequest duplicatePhone = new RegisterRequest("anotherUser", "validPass1!", "이몽룡", phone);

        // when
        ResultActions result = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicatePhone)));

        // then
        result.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 회원가입 요청을 하면 400 상태코드를 반환한다.")
    void shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // given
        RegisterRequest invalid = new RegisterRequest("a", "123", "", "010");

        // when
        ResultActions result = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)));

        // then
        result.andExpect(status().isBadRequest());
    }

}
