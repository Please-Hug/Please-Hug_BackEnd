package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.global.common.service.UserService;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*
    테스트를 위해서 시크릿키를 넣었음
    프로덕션 환경에선 다른 시크릿키를 사용해야함
*/
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("로그인 컨트롤러 테스트")
@TestPropertySource(properties = {
        "jwt.secret=aHR0cHM6Ly9naXRodWIuY29tL3NldW5nd29vay9qd3QtYXBpLXNlcnZlci1zYW1wbGUteW91LWNhbi11c2UtdGhpcy1sb25nLXNlY3JldC1rZXktZm9yLWVuY3J5cHRpb24K",
        "jwt.access-token-expiration=1800000",
        "jwt.refresh-token-expiration=604800000"
})
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("testpassword1!");
        request.setName("홍길동");
        request.setPhoneNumber("010-1234-5678");

        userService.registerNewUser(request);
    }

    @AfterEach
    void tearDown() {
        userService.deleteByUsername("testuser"); // 반드시 UserService에 해당 메서드 존재해야 함
    }

    @Test
    @DisplayName("로그인 성공시 토큰이 응답된다.")
    void shouldReturnTokens_whenLoginIsSuccessful() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpassword1!");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다"));
    }

    @Test
    @DisplayName("존재하지 않는 아이디를 입력하면 실패한다.")
    void shouldFailLogin_whenUserDoesNotExist() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("notexist");
        request.setPassword("testpassword1!");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다."));
    }


    @Test
    @DisplayName("틀린 비밀번호를 입력하면 실패한다.")
    void shouldFailLogin_whenPasswordIsIncorrect() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다."));
    }
}